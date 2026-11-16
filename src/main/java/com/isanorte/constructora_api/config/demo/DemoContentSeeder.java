package com.isanorte.constructora_api.config.demo;

import static com.isanorte.constructora_api.config.demo.DemoSupport.id;
import static com.isanorte.constructora_api.config.demo.DemoSupport.ts;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

/**
 * Fase 1 de la carga de demostración: sitio, contenido de ISANORTE, anuncios, catálogos y categorías.
 *
 * <p>Idempotente: las filas nuevas usan ids deterministas y las que ya sembró Flyway (V4) solo reciben
 * sus imágenes de Cloudinary y los textos que estaban vacíos. Todas las imágenes salen del manifiesto.
 */
@Component
@RequiredArgsConstructor
@Order(10)
@ConditionalOnProperty(name = "app.seed.demo-data", havingValue = "true")
public class DemoContentSeeder implements ApplicationRunner {
    private static final Logger log = LoggerFactory.getLogger(DemoContentSeeder.class);
    static final String SITE_KEY = "isanorte";
    static final String UNIT_SLUG = "isadecor";

    private final DemoSupport support;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        JdbcTemplate jdbc = support.jdbc();
        UUID empresa = support.requireId("select id from empresa order by fecha_creacion");
        UUID sitio = support.requireId("select id from configuraciones_sitio where clave = ?", SITE_KEY);
        UUID unidad = support.requireId("select id from unidades_negocio where slug = ?", UNIT_SLUG);
        LocalDateTime now = DemoSupport.now();

        seedSitio(jdbc, empresa, sitio, now);
        seedServicios(jdbc, now);
        seedProyectos(jdbc, now);
        seedLanding(jdbc, sitio, unidad, now);
        seedAnuncios(jdbc, now);
        seedCategorias(jdbc, unidad, now);
        seedCatalogos(jdbc, unidad, now);
        log.info("Datos demo (contenido) cargados.");
    }

    // ------------------------------------------------------------------ sitio y empresa
    private void seedSitio(JdbcTemplate jdbc, UUID empresa, UUID sitio, LocalDateTime now) {
        jdbc.update("""
                update empresa set
                  direccion = coalesce(nullif(direccion, ''), ?),
                  ciudad = coalesce(nullif(ciudad, ''), ?),
                  telefono = coalesce(nullif(telefono, ''), ?),
                  telefono_secundario = coalesce(nullif(telefono_secundario, ''), ?),
                  email = coalesce(nullif(email, ''), ?),
                  email_ventas = coalesce(nullif(email_ventas, ''), ?),
                  whatsapp = coalesce(nullif(whatsapp, ''), ?),
                  horario_atencion = coalesce(nullif(horario_atencion, ''), ?)
                where id = ?""",
                "Av. De los Shyris N35-52 y Portugal, Edificio Torre Nova, Of. 504", "Quito",
                "+593 2 245 8830", "+593 2 245 8831", "contacto@isanorte.ec", "ventas@isanorte.ec",
                "+593 99 812 4455", "Lunes a viernes de 08:30 a 18:00, sábados de 09:00 a 13:00", empresa);

        jdbc.update("""
                update configuraciones_sitio set
                  logo_url = ?, logo_blanco_url = ?, favicon_url = ?,
                  color_primario = coalesce(nullif(color_primario, ''), ?),
                  color_secundario = coalesce(nullif(color_secundario, ''), ?),
                  fecha_actualizacion = ?
                where id = ?""",
                support.url("logo-isanorte-horizontal"), support.url("logo-isanorte-horizontal-white"),
                support.url("logo-isanorte-isotipo"), "#141414", "#F26A1B", ts(now), sitio);

        String[][] redes = {
                {"Facebook", "https://www.facebook.com/isanorte.ec", "facebook"},
                {"Instagram", "https://www.instagram.com/isanorte.ec", "instagram"},
                {"LinkedIn", "https://www.linkedin.com/company/isanorte-ec", "linkedin"},
                {"YouTube", "https://www.youtube.com/@isanorte", "youtube"},
        };
        int orden = 0;
        for (String[] red : redes) {
            UUID rid = id("redes_sociales", red[0]);
            if (!support.exists("select 1 from redes_sociales where empresa_id = ? and nombre = ?", empresa, red[0])) {
                support.insertIfAbsent("redes_sociales", rid, """
                        insert into redes_sociales (id, nombre, url, icono, orden, activo, empresa_id, fecha_creacion, fecha_actualizacion)
                        values (?, ?, ?, ?, ?, true, ?, ?, ?)""",
                        rid, red[0], red[1], red[2], orden, empresa, ts(now), ts(now));
            }
            orden++;
        }
    }

    // ------------------------------------------------------------------ servicios
    private void seedServicios(JdbcTemplate jdbc, LocalDateTime now) {
        for (String slug : List.of("construccion", "acabados", "decoracion")) {
            jdbc.update("update servicios set imagen_url = ?, imagen_alt = coalesce(nullif(imagen_alt, ''), ?) where slug = ?",
                    support.url("servicio-" + slug), "Servicio de " + slug + " de ISANORTE", slug);
        }
        Object[][] nuevos = {
                {"diseno-arquitectonico", "Diseño arquitectónico", "Proyectos a medida desde el primer trazo",
                        "Desarrollamos anteproyectos, planos técnicos y renders 3D que alinean estética, normativa y presupuesto antes de iniciar la obra.",
                        "Diseño y planos", "servicio-diseno", 3,
                        new String[] {"Anteproyecto y planos técnicos", "Renders 3D del proyecto", "Coordinación con permisos municipales"}},
                {"remodelacion", "Remodelación y renovación", "Renovamos espacios sin detener tu operación",
                        "Intervenimos viviendas, oficinas y locales comerciales con planificación por fases, control de polvo y entrega puntual.",
                        "Renovación", "servicio-remodelacion", 4,
                        new String[] {"Planificación por fases", "Control de obra y limpieza diaria", "Garantía de acabados"}},
        };
        for (Object[] s : nuevos) {
            String slug = (String) s[0];
            UUID sid = id("servicios", slug);
            if (support.exists("select 1 from servicios where slug = ?", slug)) {
                continue;
            }
            jdbc.update("""
                    insert into servicios (id, nombre, slug, resumen, descripcion, imagen_url, imagen_alt, etiqueta, activo, destacado, orden, fecha_creacion, fecha_actualizacion)
                    values (?, ?, ?, ?, ?, ?, ?, ?, true, false, ?, ?, ?)""",
                    sid, s[1], slug, s[2], s[3], support.url((String) s[5]), "Servicio de " + s[1], s[4], s[6], ts(now), ts(now));
            int i = 0;
            for (String beneficio : (String[]) s[7]) {
                UUID bid = id("beneficios_servicio", slug + ":" + i);
                jdbc.update("""
                        insert into beneficios_servicio (id, texto, orden, activo, servicio_id, fecha_creacion, fecha_actualizacion)
                        values (?, ?, ?, true, ?, ?, ?)""", bid, beneficio, i++, sid, ts(now), ts(now));
            }
        }
    }

    // ------------------------------------------------------------------ proyectos
    private void seedProyectos(JdbcTemplate jdbc, LocalDateTime now) {
        // slug, cliente, ubicación, fecha, descripción, servicios
        Object[][] proyectos = {
                {"residencia-aura", "Familia Cárdenas", "Valle de Los Chillos, Quito", "Proyecto 2024",
                        "Residencia de 420 m² con fachada de piedra y madera, jardín interior y acabados en paneles WPC. Entregada en 11 meses.",
                        List.of("construccion", "acabados")},
                {"edificio-tech-corporate", "Grupo Andino Tech", "Sector financiero, Quito", "Proyecto 2023",
                        "Edificio corporativo de ocho pisos con fachada de vidrio, cielos rasos acústicos y oficinas modulares.",
                        List.of("construccion")},
                {"villa-lomas-alta", "Inversiones Lomas", "Cumbayá, Quito", "Proyecto 2023",
                        "Villa en ladera con piscina infinita, terrazas y obra civil estructural adaptada a pendiente.",
                        List.of("construccion")},
                {"penthouse-abisal", "Cliente reservado", "La Carolina, Quito", "Proyecto 2024",
                        "Penthouse de 280 m² con decoración en tonos oscuros, paneles de nogal y iluminación arquitectónica.",
                        List.of("decoracion", "acabados")},
                {"oficinas-nexus", "Nexus Consultores", "Av. Naciones Unidas, Quito", "Proyecto 2022",
                        "Oficinas abiertas de 900 m² con cielos de lamas de madera, salas de reunión de vidrio y mobiliario a medida.",
                        List.of("decoracion", "acabados")},
                {"clinica-sanitas-norte", "Clínica Sanitas", "Carcelén, Quito", "Proyecto 2022",
                        "Clínica privada de tres plantas con acabados antibacteriales, pisos SPC y señalética integrada.",
                        List.of("construccion", "acabados")},
                {"showroom-isadecor", "ISADECOR", "Av. 6 de Diciembre, Quito", "Proyecto 2024",
                        "Showroom de 350 m² para exhibir paneles, revestimientos y pisos con iluminación de museo.",
                        List.of("decoracion")},
                {"conjunto-los-pinos", "Constructora Pinos", "Tumbaco, Quito", "Proyecto 2023",
                        "Conjunto de 24 casas adosadas con áreas verdes comunales y cerramiento perimetral.",
                        List.of("construccion", "diseno-arquitectonico")},
                {"restaurante-raiz", "Grupo Raíz", "La Floresta, Quito", "Proyecto 2024",
                        "Restaurante de cocina andina con muros de ladrillo, paneles de madera y barra de mármol.",
                        List.of("decoracion", "remodelacion")},
                {"hotel-boutique-andino", "Hoteles del Valle", "Centro Histórico, Quito", "Proyecto 2025",
                        "Rehabilitación de casona patrimonial en hotel boutique de 18 habitaciones, respetando fachada original.",
                        List.of("remodelacion", "diseno-arquitectonico")},
                {"centro-comercial-plaza-norte", "Plaza Norte S.A.", "Calderón, Quito", "Proyecto 2025",
                        "Centro comercial de 12 000 m² con atrio de vidrio, locales ancla y estacionamiento subterráneo.",
                        List.of("construccion", "diseno-arquitectonico")},
        };
        int orden = 0;
        for (Object[] p : proyectos) {
            String slug = (String) p[0];
            String imagen = support.url("proyecto-" + slug);
            String alt = "Proyecto " + slug.replace('-', ' ');
            boolean existe = support.exists("select 1 from proyectos where slug = ?", slug);
            if (existe) {
                jdbc.update("""
                        update proyectos set imagen_url = ?, imagen_alt = coalesce(nullif(imagen_alt, ''), ?),
                          cliente = coalesce(nullif(cliente, ''), ?), ubicacion = coalesce(nullif(ubicacion, ''), ?),
                          fecha_proyecto = coalesce(nullif(fecha_proyecto, ''), ?),
                          descripcion = case when descripcion is null or btrim(descripcion) = '' then ? else descripcion end
                        where slug = ?""", imagen, alt, p[1], p[2], p[3], p[4], slug);
            } else {
                UUID pid = id("proyectos", slug);
                jdbc.update("""
                        insert into proyectos (id, nombre, slug, cliente, ubicacion, fecha_proyecto, descripcion, imagen_url, imagen_alt, destacado, activo, orden, fecha_creacion, fecha_actualizacion)
                        values (?, ?, ?, ?, ?, ?, ?, ?, ?, false, true, ?, ?, ?)""",
                        pid, titulo(slug), slug, p[1], p[2], p[3], p[4], imagen, alt, orden, ts(now), ts(now));
            }
            UUID pid = support.requireId("select id from proyectos where slug = ?", slug);
            @SuppressWarnings("unchecked")
            List<String> servicios = (List<String>) p[5];
            for (String servicio : servicios) {
                UUID sid = support.findId("select id from servicios where slug = ?", servicio);
                if (sid != null && !support.exists("select 1 from proyecto_servicio where proyecto_id = ? and servicio_id = ?", pid, sid)) {
                    jdbc.update("insert into proyecto_servicio (proyecto_id, servicio_id) values (?, ?)", pid, sid);
                }
            }
            orden++;
        }
    }

    private static String titulo(String slug) {
        StringBuilder sb = new StringBuilder();
        for (String part : slug.split("-")) {
            if (sb.length() > 0) sb.append(' ');
            sb.append(Character.toUpperCase(part.charAt(0))).append(part.substring(1));
        }
        return sb.toString();
    }

    // ------------------------------------------------------------------ landing, recursos, páginas y SEO
    private void seedLanding(JdbcTemplate jdbc, UUID sitio, UUID unidad, LocalDateTime now) {
        String[] escenas = {"hero-exterior", "hero-sala", "hero-cocina", "hero-bano", "hero-dormitorio"};
        UUID hero = support.findId("select id from secciones_landing where configuracion_sitio_id = ? and tipo = 'HERO'", sitio);
        if (hero != null) {
            jdbc.update("update secciones_landing set imagen_url = ?, imagen_alt = coalesce(nullif(imagen_alt, ''), ?) where id = ?",
                    support.url("hero-exterior"), "Exterior de una residencia moderna al atardecer", hero);
            List<UUID> existentes = jdbc.query("select id from hero_scenes where seccion_landing_id = ? order by orden, id",
                    (rs, i) -> rs.getObject(1, UUID.class), hero);
            for (int i = 0; i < escenas.length; i++) {
                String url = support.url(escenas[i]);
                String alt = "Ambiente " + escenas[i].substring(5) + " de un proyecto ISANORTE";
                if (i < existentes.size()) {
                    jdbc.update("update hero_scenes set imagen_url = ?, alt = ? where id = ?", url, alt, existentes.get(i));
                } else {
                    UUID hid = id("hero_scenes", escenas[i]);
                    support.insertIfAbsent("hero_scenes", hid, """
                            insert into hero_scenes (id, imagen_url, alt, orden, activo, seccion_landing_id, fecha_creacion, fecha_actualizacion)
                            values (?, ?, ?, ?, true, ?, ?, ?)""", hid, url, alt, i, hero, ts(now), ts(now));
                }
            }
        }
        jdbc.update("update secciones_landing set imagen_url = ?, imagen_alt = coalesce(nullif(imagen_alt, ''), ?) where configuracion_sitio_id = ? and tipo = 'CTA'",
                support.url("cta-consultoria"), "Asesora mostrando muestras de materiales a una pareja", sitio);

        jdbc.update("update unidades_negocio set imagen_url = ?, imagen_alt = coalesce(nullif(imagen_alt, ''), ?) where id = ?",
                support.url("isadecor-fondo"), "Muro revestido con paneles de madera en un interior oscuro", unidad);
        jdbc.update("update recursos_unidad_negocio set url = ? where unidad_negocio_id = ? and tipo = 'IMAGEN_FONDO'",
                support.url("isadecor-fondo"), unidad);
        List<UUID> editoriales = jdbc.query("select id from recursos_unidad_negocio where unidad_negocio_id = ? and tipo = 'IMAGEN_EDITORIAL' order by orden, id",
                (rs, i) -> rs.getObject(1, UUID.class), unidad);
        for (int i = 0; i < editoriales.size() && i < 3; i++) {
            jdbc.update("update recursos_unidad_negocio set url = ?, alt = coalesce(nullif(alt, ''), ?) where id = ?",
                    support.url("isadecor-editorial-" + (i + 1)), "Detalle de acabados ISADECOR", editoriales.get(i));
        }

        String[][] paginas = {
                {"NOSOTROS", "hero-sala", "Equipo de ISANORTE revisando un proyecto"},
                {"SERVICIOS", "servicio-acabados", "Instalación de revestimientos"},
                {"PROYECTOS", "proyecto-residencia-aura", "Residencia Aura"},
                {"CONTACTO", "cta-consultoria", "Asesoría personalizada"},
        };
        for (String[] pg : paginas) {
            // Se reemplazan también las rutas locales (/images/...) que dejó la migración inicial.
            jdbc.update("""
                    update contenidos_pagina set
                      imagen_url = case when imagen_url is null or imagen_url = '' or imagen_url like '/%' then ? else imagen_url end,
                      imagen_fondo_url = case when imagen_fondo_url like '/%' then ? else imagen_fondo_url end,
                      imagen_alt = coalesce(nullif(imagen_alt, ''), ?)
                    where configuracion_sitio_id = ? and pagina = ?""", support.url(pg[1]), support.url("isadecor-fondo"), pg[2], sitio, pg[0]);
        }
        String[][] seo = {
                {"HOME", "hero-exterior"}, {"NOSOTROS", "hero-sala"}, {"SERVICIOS", "servicio-construccion"},
                {"PROYECTOS", "proyecto-residencia-aura"}, {"CONTACTO", "cta-consultoria"},
        };
        for (String[] s : seo) {
            jdbc.update("update seo_paginas set og_image_url = case when og_image_url is null or og_image_url = '' or og_image_url like '/%' then ? else og_image_url end where configuracion_sitio_id = ? and tipo_pagina = ? and unidad_negocio_id is null",
                    support.url(s[1]), sitio, s[0]);
        }
        jdbc.update("update seo_paginas set og_image_url = case when og_image_url is null or og_image_url = '' or og_image_url like '/%' then ? else og_image_url end where unidad_negocio_id = ?",
                support.url("isadecor-hero"), unidad);
    }

    // ------------------------------------------------------------------ anuncios
    private void seedAnuncios(JdbcTemplate jdbc, LocalDateTime now) {
        LocalDateTime inicio = now.minusDays(10);
        LocalDateTime fin = now.plusDays(90);
        // clave, título, resumen, destino, ubicación, tipo acción, destino acción, botón, etiqueta, hover
        Object[][] anuncios = {
                {"wall-panels", "Wall panels con 18 % de descuento", "Renueva tus muros con paneles WPC de instalación rápida.", "ISADECOR", "PROMOCION_PRINCIPAL", "CATEGORIA", "wall-panels", "Ver wall panels", "Promoción", true},
                {"pisos-spc", "Pisos SPC listos para instalar", "Resistentes al agua, con 25 años de garantía residencial.", "ISADECOR", "PROMOCION_SECUNDARIA", "CATEGORIA", "pisos", "Ver pisos", "Nuevo", true},
                {"revestimientos", "Revestimientos que imitan piedra", "Travertino, pizarra y cuarzo para fachadas y baños.", "ISADECOR", "PROMOCION_SECUNDARIA", "CATEGORIA", "revestimientos", "Explorar colección", "Destacado", true},
                {"cielos-rasos", "Cielos rasos de PVC", "Acabado liso, antihumedad y fácil mantenimiento.", "ISADECOR", "ANUNCIOS_HOME", "RUTA_INTERNA", "/isadecor", "Conocer más", "Novedad", false},
                {"asesoria", "Asesoría gratuita de materiales", "Agenda una visita y recibe una propuesta sin costo.", "AMBOS", "ANUNCIOS_HOME", "RUTA_INTERNA", "/contacto", "Agendar asesoría", "Gratis", false},
                {"obra-nueva", "Construimos tu obra de principio a fin", "Planos, permisos y ejecución con un solo equipo.", "ISANORTE", "ANUNCIOS_HOME", "RUTA_INTERNA", "/servicios", "Ver servicios", "Servicios", false},
                {"instalacion", "Instalación profesional incluida", "Cotiza materiales con instalación certificada en Quito.", "ISADECOR", "CATEGORIA_SUPERIOR", "RUTA_INTERNA", "/isadecor/cotizacion", "Cotizar ahora", "Instalación", false},
                {"dormitorio", "Dormitorios con carácter", "Cabeceros de listones y paneles acústicos para descansar mejor.", "ISADECOR", "GENERAL", "CATEGORIA", "paneles-listonados", "Ver listonados", "Inspiración", false},
                {"oficinas", "Oficinas silenciosas y elegantes", "Paneles acústicos que mejoran la concentración del equipo.", "AMBOS", "CINTA_HOME", "CATEGORIA", "paneles-acusticos", "Ver acústicos", "Corporativo", false},
                {"exteriores", "Proyectos ISANORTE en Quito", "Conoce nuestras obras entregadas en los últimos años.", "ISANORTE", "CINTA_HOME", "RUTA_INTERNA", "/proyectos", "Ver proyectos", "Portafolio", false},
        };
        int orden = 0;
        for (Object[] a : anuncios) {
            String clave = (String) a[0];
            UUID aid = id("anuncios", clave);
            String hover = (boolean) a[9] ? support.url("anuncio-" + clave + "-hover") : null;
            support.insertIfAbsent("anuncios", aid, """
                    insert into anuncios (id, titulo, descripcion_resumida, contenido_detallado, condiciones, imagen_url, imagen_hover_url,
                      etiqueta, destino, ubicacion, tipo_accion, destino_accion, texto_boton, fecha_inicio, fecha_fin, activo, orden,
                      fecha_creacion, fecha_actualizacion)
                    values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, true, ?, ?, ?)""",
                    aid, a[1], a[2], a[2] + " Solicita tu cotización y un asesor te contactará en menos de 24 horas.",
                    "Promoción sujeta a disponibilidad de stock. Aplica en Quito y valles.",
                    support.url("anuncio-" + clave), hover, a[8], a[3], a[4], a[5], a[6], a[7], ts(inicio), ts(fin), orden++, ts(now), ts(now));
        }
        // Un anuncio vencido y otro programado para que el panel muestre todos los estados de vigencia.
        UUID vencido = id("anuncios", "vencido");
        support.insertIfAbsent("anuncios", vencido, """
                insert into anuncios (id, titulo, descripcion_resumida, contenido_detallado, imagen_url, etiqueta, destino, ubicacion,
                  tipo_accion, destino_accion, texto_boton, fecha_inicio, fecha_fin, activo, orden, fecha_creacion, fecha_actualizacion)
                values (?, ?, ?, ?, ?, 'Finalizada', 'ISADECOR', 'GENERAL', 'RUTA_INTERNA', '/isadecor', 'Ver tienda', ?, ?, true, ?, ?, ?)""",
                vencido, "Feria de acabados de primavera", "Campaña finalizada.", "Campaña de descuentos de temporada finalizada.",
                support.url("anuncio-exteriores"), ts(now.minusDays(60)), ts(now.minusDays(20)), orden++, ts(now), ts(now));
        UUID programado = id("anuncios", "programado");
        support.insertIfAbsent("anuncios", programado, """
                insert into anuncios (id, titulo, descripcion_resumida, contenido_detallado, imagen_url, etiqueta, destino, ubicacion,
                  tipo_accion, destino_accion, texto_boton, fecha_inicio, fecha_fin, activo, orden, fecha_creacion, fecha_actualizacion)
                values (?, ?, ?, ?, ?, 'Próximamente', 'AMBOS', 'GENERAL', 'RUTA_INTERNA', '/contacto', 'Avísame', ?, ?, true, ?, ?, ?)""",
                programado, "Gran liquidación de fin de año", "Prepárate: descuentos en toda la tienda.", "Campaña programada para fin de año.",
                support.url("anuncio-obra-nueva"), ts(now.plusDays(40)), ts(now.plusDays(70)), orden, ts(now), ts(now));
    }

    // ------------------------------------------------------------------ categorías de producto
    private void seedCategorias(JdbcTemplate jdbc, UUID unidad, LocalDateTime now) {
        String[][] categorias = {
                {"wall-panels", "Wall Panels", "Paneles murales decorativos para interiores.", "cat-wall-panels"},
                {"revestimientos", "Revestimientos", "Revestimientos de piedra, ladrillo y cemento para muros.", "cat-revestimientos"},
                {"pisos", "Pisos", "Pisos SPC y de madera para hogar y comercio.", "cat-pisos"},
                {"paneles-listonados", "Paneles listonados", "Listones verticales de madera para muros y cabeceros.", "cat-paneles-listonados"},
                {"paneles-acusticos", "Paneles acústicos", "Paneles que absorben el ruido en oficinas y salas.", "cat-paneles-acusticos"},
                {"paneles-decorativos", "Paneles decorativos", "Paneles geométricos y ondulados en relieve 3D.", "cat-paneles-decorativos"},
        };
        int orden = 0;
        for (String[] c : categorias) {
            UUID cid = support.findId("select id from categorias_producto where slug = ?", c[0]);
            if (cid == null) {
                cid = id("categorias_producto", c[0]);
                jdbc.update("""
                        insert into categorias_producto (id, nombre, slug, descripcion, imagen_url, activo, orden, unidad_negocio_id, fecha_creacion, fecha_actualizacion)
                        values (?, ?, ?, ?, ?, true, ?, ?, ?, ?)""", cid, c[1], c[0], c[2], support.url(c[3]), orden, unidad, ts(now), ts(now));
            } else {
                jdbc.update("update categorias_producto set imagen_url = ? where id = ?", support.url(c[3]), cid);
            }
            orden++;
        }
    }

    // ------------------------------------------------------------------ catálogos
    private void seedCatalogos(JdbcTemplate jdbc, UUID unidad, LocalDateTime now) {
        String[][] catalogos = {
                {"catalogo-wall-panels", "Catálogo Wall Panels 2026"},
                {"catalogo-pisos", "Catálogo Pisos SPC y madera 2026"},
                {"catalogo-revestimientos", "Catálogo Revestimientos 2026"},
        };
        int orden = 0;
        for (String[] c : catalogos) {
            UUID cid = id("catalogos", c[0]);
            if (!support.insertIfAbsent("catalogos", cid, """
                    insert into catalogos (id, titulo, imagen_portada_url, archivo_url, orden, activo, unidad_negocio_id, fecha_creacion, fecha_actualizacion)
                    values (?, ?, ?, ?, ?, true, ?, ?, ?)""",
                    cid, c[1], support.url(c[0]), support.url(c[0] + "-pdf"), orden, unidad, ts(now), ts(now))) {
                jdbc.update("update catalogos set imagen_portada_url = ?, archivo_url = ? where id = ?",
                        support.url(c[0]), support.url(c[0] + "-pdf"), cid);
            }
            orden++;
        }
    }
}
