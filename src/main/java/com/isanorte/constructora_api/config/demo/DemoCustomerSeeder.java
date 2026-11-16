package com.isanorte.constructora_api.config.demo;

import static com.isanorte.constructora_api.config.demo.DemoSupport.EMAIL_DOMAIN;
import static com.isanorte.constructora_api.config.demo.DemoSupport.id;
import static com.isanorte.constructora_api.config.demo.DemoSupport.ts;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import tools.jackson.databind.ObjectMapper;

/**
 * Fase 2 de la carga de demostración (después del catálogo): datos de producto que dependen de él,
 * clientes, direcciones, carritos, solicitudes de contacto, cotizaciones, pedidos, reseñas y accesos.
 *
 * <p>Respeta las reglas del dominio: totales = Σ(precio × cantidad) sin IVA con snapshot en cada línea,
 * stock descontado solo en pedidos no cancelados, último seguimiento = estado de la cabecera y códigos
 * {@code COT-/PED-yyyyMMdd-XXXXXXXX}. Los ids son deterministas, así que repetir la carga no duplica.
 */
@Component
@RequiredArgsConstructor
@Order(30)
@ConditionalOnProperty(name = "app.seed.demo-data", havingValue = "true")
public class DemoCustomerSeeder implements ApplicationRunner {
    private static final Logger log = LoggerFactory.getLogger(DemoCustomerSeeder.class);
    private static final DateTimeFormatter CODE_DATE = DateTimeFormatter.ofPattern("yyyyMMdd");

    private final DemoSupport support;
    private final PasswordEncoder passwordEncoder;
    private final ObjectMapper objectMapper;

    record Prod(UUID id, String slug, String nombre, String sku, BigDecimal precio, String categoria, String disponibilidad,
            Integer stock, String imagen) { }

    record Variante(UUID id, String sku, String nombre, BigDecimal precio) { }

    record Cli(UUID id, String nombre, String apellido, String email, String telefono, LocalDateTime creado) { }

    record Linea(Prod producto, Variante variante, int cantidad) {
        BigDecimal precioUnitario() {
            return variante != null && variante.precio() != null ? variante.precio() : producto.precio();
        }
        BigDecimal subtotal() {
            return precioUnitario() == null ? null : precioUnitario().multiply(BigDecimal.valueOf(cantidad));
        }
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        JdbcTemplate jdbc = support.jdbc();
        LocalDateTime now = DemoSupport.now();
        Random random = new Random(2026);

        seedAccesos(jdbc, now);
        seedProductExtras(jdbc);
        List<Prod> productos = loadProducts(jdbc);
        List<Cli> clientes = seedClientes(jdbc, now);
        seedDirecciones(jdbc, clientes, now);
        seedCarritos(jdbc, clientes, productos, now);
        seedSolicitudes(jdbc, now);
        UUID admin = support.findId("select id from administradores order by fecha_creacion");
        seedCotizaciones(jdbc, clientes, productos, admin, now, random);
        seedPedidos(jdbc, clientes, loadProducts(jdbc), admin, now, random);
        seedResenas(jdbc, clientes, productos, now, random);
        log.info("Datos demo (clientes, cotizaciones, pedidos y reseñas) cargados.");
    }

    // ------------------------------------------------------------------ accesos
    private void seedAccesos(JdbcTemplate jdbc, LocalDateTime now) {
        String[][] roles = {
                {"VENTAS", "Gestión de cotizaciones, pedidos y clientes"},
                {"CONTENIDO", "Gestión de contenido, anuncios y catálogo"},
        };
        for (String[] r : roles) {
            UUID rid = id("roles", r[0]);
            if (!support.exists("select 1 from roles where nombre = ?", r[0])) {
                jdbc.update("insert into roles (id, nombre, descripcion, activo, fecha_creacion, fecha_actualizacion) values (?, ?, ?, true, ?, ?)",
                        rid, r[0], r[1], ts(now), ts(now));
            }
        }
        String[][] permisos = {
                {"PRODUCTOS_LEER", "Ver productos", "PRODUCTOS", "VENTAS,CONTENIDO"},
                {"PRODUCTOS_EDITAR", "Editar productos", "PRODUCTOS", "CONTENIDO"},
                {"COTIZACIONES_LEER", "Ver cotizaciones", "COTIZACIONES", "VENTAS"},
                {"COTIZACIONES_GESTIONAR", "Gestionar cotizaciones", "COTIZACIONES", "VENTAS"},
                {"PEDIDOS_LEER", "Ver pedidos", "PEDIDOS", "VENTAS"},
                {"PEDIDOS_GESTIONAR", "Gestionar pedidos", "PEDIDOS", "VENTAS"},
                {"ANUNCIOS_EDITAR", "Editar anuncios", "ANUNCIOS", "CONTENIDO"},
                {"CONTENIDO_EDITAR", "Editar contenido del sitio", "CONTENIDO", "CONTENIDO"},
        };
        for (String[] p : permisos) {
            UUID pid = support.findId("select id from permisos where codigo = ?", p[0]);
            if (pid == null) {
                pid = id("permisos", p[0]);
                jdbc.update("insert into permisos (id, codigo, nombre, descripcion, modulo, fecha_creacion) values (?, ?, ?, ?, ?, ?)",
                        pid, p[0], p[1], p[1] + " del panel administrativo", p[2], ts(now));
            }
            for (String rol : p[3].split(",")) {
                UUID rid = support.requireId("select id from roles where nombre = ?", rol);
                if (!support.exists("select 1 from rol_permiso where rol_id = ? and permiso_id = ?", rid, pid)) {
                    jdbc.update("insert into rol_permiso (rol_id, permiso_id) values (?, ?)", rid, pid);
                }
            }
        }
    }

    // ------------------------------------------------------------------ productos: stock, destacados y documentos
    private void seedProductExtras(JdbcTemplate jdbc) {
        List<Map<String, Object>> rows = jdbc.queryForList("""
                select p.id, p.slug, p.nombre, p.destacado, p.stock_actual, c.slug as categoria
                from productos p
                join unidades_negocio u on u.id = p.unidad_negocio_id and u.slug = 'isadecor'
                left join categoria_producto cp on cp.producto_id = p.id
                left join categorias_producto c on c.id = cp.categoria_id
                where p.estado = 'PUBLICADO' order by p.sku""");
        int destacado = 0;
        LocalDateTime now = DemoSupport.now();
        for (Map<String, Object> row : rows) {
            UUID pid = (UUID) row.get("id");
            String slug = (String) row.get("slug");
            if (Boolean.TRUE.equals(row.get("destacado"))) {
                jdbc.update("update productos set orden_destacado = ? where id = ?", destacado++, pid);
            }
            // Inventario gestionado en ~70 % de los productos; los demás se venden bajo consulta de stock.
            if (row.get("stock_actual") == null && Math.floorMod(slug.hashCode(), 10) < 7) {
                boolean agotado = slug.equals("piso-espiga-miel") || slug.equals("revestimiento-onix-ambar");
                int stock = agotado ? 0 : 60 + Math.floorMod(slug.hashCode(), 240);
                jdbc.update("update productos set stock_actual = ?, disponibilidad = case when ? then 'AGOTADO' else disponibilidad end where id = ?",
                        stock, agotado, pid);
            }
            String categoria = (String) row.get("categoria");
            String pdf = switch (categoria == null ? "" : categoria) {
                case "pisos" -> "catalogo-pisos-pdf";
                case "revestimientos" -> "catalogo-revestimientos-pdf";
                default -> "catalogo-wall-panels-pdf";
            };
            UUID did = id("documentos_producto", slug);
            support.insertIfAbsent("documentos_producto", did, """
                    insert into documentos_producto (id, titulo, tipo, url, formato, tamano_bytes, producto_id, fecha_creacion)
                    values (?, ?, 'CATALOGO', ?, 'pdf', ?, ?, ?)""",
                    did, "Catálogo de la línea — " + row.get("nombre"), support.url(pdf),
                    900_000L + Math.floorMod(slug.hashCode(), 600_000), pid, ts(now));
        }
    }

    private List<Prod> loadProducts(JdbcTemplate jdbc) {
        return jdbc.query("""
                select p.id, p.slug, p.nombre, p.sku, p.precio_base, p.disponibilidad, p.stock_actual,
                  (select c.slug from categoria_producto cp join categorias_producto c on c.id = cp.categoria_id where cp.producto_id = p.id order by c.orden limit 1) as categoria,
                  (select i.url from imagenes_producto i where i.producto_id = p.id order by i.es_principal desc, i.orden limit 1) as imagen
                from productos p join unidades_negocio u on u.id = p.unidad_negocio_id and u.slug = 'isadecor'
                where p.estado = 'PUBLICADO' and p.precio_base is not null order by p.sku""",
                (rs, i) -> new Prod(rs.getObject("id", UUID.class), rs.getString("slug"), rs.getString("nombre"), rs.getString("sku"),
                        rs.getBigDecimal("precio_base"), rs.getString("categoria"), rs.getString("disponibilidad"),
                        (Integer) rs.getObject("stock_actual"), rs.getString("imagen")));
    }

    private List<Variante> variantes(JdbcTemplate jdbc, UUID producto) {
        return jdbc.query("select id, sku, nombre, precio from variantes_producto where producto_id = ? and disponible = true order by orden",
                (rs, i) -> new Variante(rs.getObject(1, UUID.class), rs.getString(2), rs.getString(3), rs.getBigDecimal(4)), producto);
    }

    // ------------------------------------------------------------------ clientes, direcciones y carritos
    private static final String[][] CLIENTES = {
            {"Camila", "Andrade", "0991234501"}, {"Mateo", "Villacís", "0982345602"}, {"Valeria", "Cevallos", "0963456703"},
            {"Sebastián", "Proaño", "0994567804"}, {"Daniela", "Montalvo", "0985678905"}, {"Andrés", "Naranjo", "0996789006"},
            {"Paola", "Sarmiento", "0987890107"}, {"Javier", "Guerrero", "0998901208"}, {"Lucía", "Paredes", "0969012309"},
            {"Diego", "Salazar", "0990123410"}, {"Gabriela", "Rivadeneira", "0981234511"}, {"Esteban", "Játiva", "0992345612"},
    };

    private List<Cli> seedClientes(JdbcTemplate jdbc, LocalDateTime now) {
        String password = support.clientPassword();
        if (password.isBlank()) {
            log.warn("SEED_DEMO_CLIENT_PASSWORD no está definida: las cuentas demo se crean con una clave aleatoria y no podrán iniciar sesión.");
        }
        List<Cli> out = new ArrayList<>();
        for (int i = 0; i < CLIENTES.length; i++) {
            String[] c = CLIENTES[i];
            String email = (strip(c[0]) + "." + strip(c[1]) + EMAIL_DOMAIN).toLowerCase();
            LocalDateTime creado = now.minusDays(150L - i * 7L);
            UUID cid = id("clientes", email);
            if (!support.exists("select 1 from clientes where email = ?", email)) {
                String hash = passwordEncoder.encode(password.isBlank() ? UUID.randomUUID().toString() : password);
                jdbc.update("""
                        insert into clientes (id, nombre, apellido, email, password_hash, telefono, activo, consentimiento_datos,
                          fecha_consentimiento, ultimo_acceso, fecha_creacion, fecha_actualizacion)
                        values (?, ?, ?, ?, ?, ?, true, true, ?, ?, ?, ?)""",
                        cid, c[0], c[1], email, hash, "+593" + c[2].substring(1), ts(creado), ts(now.minusDays(2L + i * 2L)), ts(creado), ts(creado));
            }
            UUID real = support.requireId("select id from clientes where email = ?", email);
            out.add(new Cli(real, c[0], c[1], email, "+593" + c[2].substring(1), creado));
        }
        return out;
    }

    private static String strip(String value) {
        return java.text.Normalizer.normalize(value, java.text.Normalizer.Form.NFD).replaceAll("\\p{M}", "").replace(" ", "");
    }

    private static final String[][] DIRECCIONES = {
            {"Casa", "Av. República del Salvador N34-127 y Suecia", "Iñaquito", "Frente al parque La Carolina"},
            {"Oficina", "Av. Amazonas N39-61 y Pereira", "La Mariscal", "Edificio Prisma, piso 7"},
            {"Casa", "Calle Los Cedros E8-20 y Av. Interoceánica", "Cumbayá", "Conjunto Las Palmas, casa 14"},
            {"Casa", "Av. 10 de Agosto N45-120 y Mariana de Jesús", "Jipijapa", "Junto a la farmacia"},
            {"Departamento", "Calle Shyris N36-12 y Naciones Unidas", "Iñaquito", "Torre Quito Plaza, depto. 902"},
            {"Casa", "Av. Simón Bolívar S/N y Los Laureles", "Tumbaco", "Urbanización Los Arrayanes"},
    };

    private void seedDirecciones(JdbcTemplate jdbc, List<Cli> clientes, LocalDateTime now) {
        for (int i = 0; i < clientes.size(); i++) {
            Cli c = clientes.get(i);
            int cantidad = i < 6 ? 2 : 1;
            for (int j = 0; j < cantidad; j++) {
                String[] d = DIRECCIONES[(i + j * 3) % DIRECCIONES.length];
                UUID did = id("direcciones_cliente", c.email() + ":" + j);
                support.insertIfAbsent("direcciones_cliente", did, """
                        insert into direcciones_cliente (id, cliente_id, alias, calle, distrito, ciudad, referencia, telefono_contacto, principal, fecha_creacion, fecha_actualizacion)
                        values (?, ?, ?, ?, ?, 'Quito', ?, ?, ?, ?, ?)""",
                        did, c.id(), j == 0 ? d[0] : "Oficina", d[1], d[2], d[3], c.telefono(), j == 0, ts(c.creado().plusDays(1)), ts(c.creado().plusDays(1)));
            }
        }
    }

    private void seedCarritos(JdbcTemplate jdbc, List<Cli> clientes, List<Prod> productos, LocalDateTime now) {
        for (int i = 8; i < clientes.size(); i++) {
            Cli c = clientes.get(i);
            if (support.exists("select 1 from carritos_cliente where cliente_id = ?", c.id())) {
                continue;
            }
            List<Map<String, Object>> items = new ArrayList<>();
            for (int k = 0; k < 2; k++) {
                Prod p = productos.get((i * 3 + k * 5) % productos.size());
                Map<String, Object> item = new LinkedHashMap<>();
                item.put("productoSlug", p.slug());
                item.put("productoNombre", p.nombre());
                item.put("productoSku", p.sku());
                item.put("imagenUrl", p.imagen());
                item.put("imagenAlt", p.nombre());
                item.put("cantidad", 4 + k * 6);
                item.put("precioUnitario", p.precio());
                items.add(item);
            }
            jdbc.update("insert into carritos_cliente (cliente_id, contenido, fecha_actualizacion) values (?, ?, ?)",
                    c.id(), objectMapper.writeValueAsString(items), ts(now.minusDays(i - 6L)));
        }
    }

    // ------------------------------------------------------------------ solicitudes de contacto
    private void seedSolicitudes(JdbcTemplate jdbc, LocalDateTime now) {
        String[][] s = {
                {"Rosa Chiliquinga", "Quisiera cotizar el revestimiento de dos fachadas en Cumbayá.", "NUEVA"},
                {"Carlos Benítez", "¿Hacen instalación de pisos SPC en departamentos de 90 m²?", "NUEVA"},
                {"Mónica Ortega", "Necesito asesoría para renovar el local de mi cafetería.", "NUEVA"},
                {"Ricardo Tapia", "Busco paneles acústicos para una sala de reuniones.", "NUEVA"},
                {"Fernanda Lasso", "Solicito visita técnica para un proyecto de casa en Tumbaco.", "EN_GESTION"},
                {"Hernán Aguirre", "Quiero ver muestras de wall panels en madera oscura.", "EN_GESTION"},
                {"Silvana Mora", "Requiero presupuesto para remodelar tres baños.", "EN_GESTION"},
                {"Pablo Cedeño", "¿Tienen catálogo de revestimientos tipo piedra?", "RESPONDIDA"},
                {"Natalia Fierro", "Consulta por tiempos de entrega de pisos en madera.", "RESPONDIDA"},
                {"Óscar Pazmiño", "Necesito cotizar la obra civil de una bodega.", "RESPONDIDA"},
                {"Inés Vega", "Me interesa el servicio de diseño arquitectónico.", "RESPONDIDA"},
                {"Mauricio Ponce", "Cotización duplicada, ya fui atendido por teléfono.", "DESCARTADA"},
                {"Teresa Bravo", "Envié el formulario dos veces por error.", "DESCARTADA"},
                {"promo-ofertas", "Compra seguidores y mejora tu posicionamiento hoy mismo.", "SPAM"},
                {"gana-dinero", "Trabaja desde casa y gana dólares sin experiencia.", "SPAM"},
        };
        for (int i = 0; i < s.length; i++) {
            String slug = strip(s[i][0]).toLowerCase();
            String email = slug + EMAIL_DOMAIN;
            UUID sid = id("solicitudes_contacto", email);
            LocalDateTime creada = now.minusDays(40L - i * 2L).withHour(9 + i % 8).withMinute(15);
            LocalDateTime actualizada = "NUEVA".equals(s[i][2]) ? creada : creada.plusHours(6 + i);
            support.insertIfAbsent("solicitudes_contacto", sid, """
                    insert into solicitudes_contacto (id, nombre, email, telefono, empresa, mensaje, estado, fecha_creacion, fecha_actualizacion)
                    values (?, ?, ?, ?, ?, ?, ?, ?, ?)""",
                    sid, s[i][0], email, "+593 9" + (80000000 + i * 1371), i % 3 == 0 ? "Particular" : null, s[i][1], s[i][2], ts(creada), ts(actualizada));
        }
    }

    // ------------------------------------------------------------------ cotizaciones
    private static final String[] COT_ESTADOS = {"NUEVA", "NUEVA", "NUEVA", "EN_REVISION", "EN_REVISION", "CONTACTADA", "CONTACTADA",
            "COTIZADA", "COTIZADA", "COTIZADA", "CERRADA", "CERRADA", "CERRADA", "CANCELADA", "CANCELADA"};
    private static final String[] COT_FLUJO = {"NUEVA", "EN_REVISION", "CONTACTADA", "COTIZADA", "CERRADA"};
    private static final String[] COT_COMENTARIOS = {"", "Se asignó la cotización a un asesor comercial.",
            "Se contactó al cliente por teléfono para validar medidas.", "Se envió la propuesta económica por correo.",
            "El cliente aceptó la propuesta y se cerró la venta."};

    private void seedCotizaciones(JdbcTemplate jdbc, List<Cli> clientes, List<Prod> productos, UUID admin, LocalDateTime now, Random random) {
        for (int i = 0; i < COT_ESTADOS.length; i++) {
            String estado = COT_ESTADOS[i];
            LocalDateTime creada = now.minusDays(75L - i * 4L).withHour(10).withMinute(5 + i);
            String codigo = "COT-" + CODE_DATE.format(creada) + "-DEMO" + String.format("%04d", i + 1);
            UUID cid = id("cotizaciones", codigo);
            List<Linea> lineas = lineas(jdbc, productos, random, 1 + i % 3, false);
            if (support.exists("select 1 from cotizaciones where id = ?", cid)) {
                continue;
            }
            Cli cliente = i < 11 ? clientes.get(i % clientes.size()) : null;
            String nombre = cliente != null ? cliente.nombre() + " " + cliente.apellido() : "Constructora " + new String[] {"Andes", "Pichincha", "Norte Verde", "Sierra"}[i % 4];
            String email = cliente != null ? cliente.email() : "compras" + i + EMAIL_DOMAIN;
            String telefono = cliente != null ? cliente.telefono() : "+593 2 290 " + (1000 + i);
            BigDecimal total = total(lineas);
            LocalDateTime actualizada = creada;
            int pasos = "CANCELADA".equals(estado) ? 2 : indexOf(COT_FLUJO, estado);
            actualizada = creada.plusDays(pasos);
            jdbc.update("""
                    insert into cotizaciones (id, codigo, nombre_cliente, email_cliente, telefono_cliente, empresa_cliente, ciudad, mensaje,
                      canal, estado, total_estimado, fecha_creacion, fecha_actualizacion, cliente_id)
                    values (?, ?, ?, ?, ?, ?, 'Quito', ?, ?, ?, ?, ?, ?, ?)""",
                    cid, codigo, nombre, email, telefono, cliente == null ? nombre : null,
                    "Solicito cotización de materiales con instalación para un proyecto en Quito.",
                    i % 2 == 0 ? "FORMULARIO" : "WHATSAPP", estado, total, ts(creada), ts(actualizada), cliente == null ? null : cliente.id());
            insertDetalles(jdbc, "detalles_cotizacion", "cotizacion_id", cid, codigo, lineas);

            // Seguimiento: la creación no genera registro; cada cambio de estado agrega uno.
            String anterior = "NUEVA";
            List<String> camino = new ArrayList<>();
            if ("CANCELADA".equals(estado)) {
                camino.add("EN_REVISION");
                camino.add("CANCELADA");
            } else {
                for (int k = 1; k <= pasos; k++) camino.add(COT_FLUJO[k]);
            }
            int k = 1;
            for (String nuevo : camino) {
                String comentario = "CANCELADA".equals(nuevo) ? "El cliente canceló la solicitud." : COT_COMENTARIOS[Math.min(k, 4)];
                jdbc.update("""
                        insert into seguimientos_cotizacion (id, estado_anterior, estado_nuevo, comentario, fecha, cotizacion_id, administrador_id)
                        values (?, ?, ?, ?, ?, ?, ?)""",
                        id("seguimientos_cotizacion", codigo + ":" + k), anterior, nuevo, comentario, ts(creada.plusDays(k)), cid, admin);
                anterior = nuevo;
                k++;
            }
        }
    }

    // ------------------------------------------------------------------ pedidos
    private static final String[] PED_ESTADOS = {"PENDIENTE_PAGO", "PENDIENTE_PAGO", "PENDIENTE_PAGO", "PAGADO_MANUAL", "PAGADO_MANUAL",
            "EN_PREPARACION", "EN_PREPARACION", "ENVIADO", "ENVIADO", "ENTREGADO", "ENTREGADO", "ENTREGADO", "ENTREGADO", "CANCELADO", "CANCELADO"};
    private static final String[] PED_FLUJO = {"PENDIENTE_PAGO", "PAGADO_MANUAL", "EN_PREPARACION", "ENVIADO", "ENTREGADO"};
    private static final String[] PED_COMENTARIOS = {"", "Pago verificado manualmente por el equipo de ventas.", "El pedido pasó a preparación en bodega.",
            "El pedido salió con transportista a la dirección indicada.", "El cliente confirmó la recepción del pedido."};

    private void seedPedidos(JdbcTemplate jdbc, List<Cli> clientes, List<Prod> productos, UUID admin, LocalDateTime now, Random random) {
        for (int i = 0; i < PED_ESTADOS.length; i++) {
            String estado = PED_ESTADOS[i];
            LocalDateTime creado = now.minusDays(62L - i * 4L).withHour(11).withMinute(10 + i);
            String codigo = "PED-" + CODE_DATE.format(creado) + "-DEMO" + String.format("%04d", i + 1);
            UUID pid = id("pedidos", codigo);
            if (support.exists("select 1 from pedidos where id = ?", pid)) {
                continue;
            }
            Cli cliente = clientes.get(i % clientes.size());
            Map<String, Object> dir = jdbc.queryForMap("select alias, calle, distrito, ciudad, referencia, telefono_contacto from direcciones_cliente where cliente_id = ? order by principal desc, fecha_creacion limit 1", cliente.id());
            boolean cancelado = "CANCELADO".equals(estado);
            List<Linea> lineas = lineas(jdbc, productos, random, 1 + i % 3, true);
            int pasos = cancelado ? 1 : indexOf(PED_FLUJO, estado);
            LocalDateTime actualizado = creado.plusDays(pasos + (cancelado ? 1 : 0));
            boolean conFactura = i % 3 == 0;
            jdbc.update("""
                    insert into pedidos (id, codigo, cliente_id, direccion_alias, direccion_calle, direccion_distrito, direccion_ciudad,
                      direccion_referencia, direccion_telefono_contacto, ruc, razon_social, estado, total_pedido, stock_descontado,
                      fecha_creacion, fecha_actualizacion)
                    values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)""",
                    pid, codigo, cliente.id(), dir.get("alias"), dir.get("calle"), dir.get("distrito"), dir.get("ciudad"), dir.get("referencia"),
                    dir.get("telefono_contacto"), conFactura ? "17900" + (10000 + i * 137) + "001" : null,
                    conFactura ? "Inversiones " + cliente.apellido() + " Cía. Ltda." : null, estado, total(lineas), !cancelado, ts(creado), ts(actualizado));
            insertDetalles(jdbc, "detalles_pedido", "pedido_id", pid, codigo, lineas);
            if (!cancelado) {
                for (Linea l : lineas) {
                    // Solo productos con inventario gestionado descuentan stock (igual que PedidoService).
                    jdbc.update("update productos set stock_actual = stock_actual - ? where id = ? and stock_actual is not null", l.cantidad(), l.producto().id());
                }
            }
            String anterior = "PENDIENTE_PAGO";
            List<String> camino = new ArrayList<>();
            if (cancelado) {
                camino.add("CANCELADO");
            } else {
                for (int k = 1; k <= pasos; k++) camino.add(PED_FLUJO[k]);
            }
            int k = 1;
            for (String nuevo : camino) {
                String comentario = cancelado ? "El cliente no completó el pago y el pedido fue cancelado; se repuso el stock." : PED_COMENTARIOS[Math.min(k, 4)];
                jdbc.update("""
                        insert into seguimientos_pedido (id, estado_anterior, estado_nuevo, comentario, fecha, pedido_id, administrador_id)
                        values (?, ?, ?, ?, ?, ?, ?)""",
                        id("seguimientos_pedido", codigo + ":" + k), anterior, nuevo, comentario, ts(creado.plusDays(k)), pid, admin);
                anterior = nuevo;
                k++;
            }
        }
    }

    // ------------------------------------------------------------------ reseñas
    private static final String[][] RESENAS_BUENAS = {
            {"Excelente calidad", "El acabado es idéntico a la muestra y la instalación fue rápida. Muy recomendado."},
            {"Superó mis expectativas", "El color se ve increíble con la luz de la sala. Llegó bien embalado."},
            {"Muy buena relación precio-calidad", "Material resistente y fácil de limpiar. Lo volvería a comprar."},
            {"Instalación sencilla", "Los paneles encajan perfecto y el resultado se ve muy elegante."},
            {"Atención impecable", "Me asesoraron con las medidas exactas y llegó todo completo."},
            {"Hermoso acabado", "La textura se siente premium. Mis visitas preguntan dónde lo compré."},
            {"Totalmente recomendable", "Cumplió con el tiempo de entrega y el producto es tal como se muestra."},
            {"Perfecto para mi proyecto", "Lo usé en una oficina y el ambiente cambió por completo."},
    };
    private static final String[][] RESENAS_MEDIAS = {
            {"Bueno, con detalles", "El producto es bonito pero una caja llegó con una esquina golpeada."},
            {"Cumple, nada más", "La calidad es correcta; esperaba un tono un poco más cálido."},
    };
    private static final String[][] RESENAS_MALAS = {
            {"Tardó más de lo esperado", "El material es bueno, pero la entrega se retrasó una semana."},
    };

    private void seedResenas(JdbcTemplate jdbc, List<Cli> clientes, List<Prod> productos, LocalDateTime now, Random random) {
        // Reseñas demo ya existentes (para no sumar más al repetir la carga).
        int total = jdbc.queryForObject(
                "select count(*) from resenas_producto r join clientes c on c.id = r.cliente_id where c.email like ?",
                Integer.class, "%" + EMAIL_DOMAIN);
        // 1) Reseñas verificadas: de clientes que recibieron el producto (pedido ENTREGADO).
        List<Map<String, Object>> entregas = jdbc.queryForList("""
                select p.cliente_id, d.producto_id, p.fecha_actualizacion
                from pedidos p join detalles_pedido d on d.pedido_id = p.id
                where p.estado = 'ENTREGADO' and p.codigo like '%-DEMO____' order by p.codigo""");
        for (Map<String, Object> e : entregas) {
            UUID cliente = (UUID) e.get("cliente_id");
            UUID producto = (UUID) e.get("producto_id");
            Cli c = clientes.stream().filter(x -> x.id().equals(cliente)).findFirst().orElse(null);
            if (c == null) continue;
            LocalDateTime fecha = ((java.sql.Timestamp) e.get("fecha_actualizacion")).toLocalDateTime().plusDays(3);
            total += insertResena(jdbc, c, producto, fecha, random, true);
        }
        // 2) Reseñas no verificadas hasta completar 40 en total.
        int intentos = 0;
        while (total < 40 && intentos++ < 400) {
            Cli c = clientes.get(random.nextInt(clientes.size()));
            Prod p = productos.get(random.nextInt(productos.size()));
            LocalDateTime fecha = now.minusDays(5L + random.nextInt(55));
            total += insertResena(jdbc, c, p.id(), fecha, random, false);
        }
    }

    private int insertResena(JdbcTemplate jdbc, Cli c, UUID producto, LocalDateTime fecha, Random random, boolean verificada) {
        UUID rid = id("resenas_producto", c.email() + ":" + producto);
        if (support.exists("select 1 from resenas_producto where id = ?", rid)
                || support.exists("select 1 from resenas_producto where cliente_id = ? and producto_id = ?", c.id(), producto)) {
            return 0;
        }
        int sorteo = random.nextInt(100);
        int calificacion = sorteo < 45 ? 5 : sorteo < 80 ? 4 : sorteo < 92 ? 3 : sorteo < 97 ? 2 : 1;
        String[] texto = calificacion >= 4 ? RESENAS_BUENAS[random.nextInt(RESENAS_BUENAS.length)]
                : calificacion == 3 ? RESENAS_MEDIAS[random.nextInt(RESENAS_MEDIAS.length)] : RESENAS_MALAS[0];
        jdbc.update("""
                insert into resenas_producto (id, producto_id, cliente_id, nombre_cliente, calificacion, titulo, comentario, fecha_creacion, compra_verificada, cantidad_util)
                values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)""",
                rid, producto, c.id(), c.nombre() + " " + c.apellido(), calificacion, texto[0], texto[1], ts(fecha), verificada, random.nextInt(10));
        return 1;
    }

    // ------------------------------------------------------------------ utilidades de líneas
    /** Elige líneas de producto; en pedidos solo productos disponibles y con stock suficiente. */
    private List<Linea> lineas(JdbcTemplate jdbc, List<Prod> productos, Random random, int cantidadLineas, boolean pedido) {
        List<Linea> out = new ArrayList<>();
        List<UUID> usados = new ArrayList<>();
        int guard = 0;
        while (out.size() < cantidadLineas && guard++ < 200) {
            Prod p = productos.get(random.nextInt(productos.size()));
            if (usados.contains(p.id())) continue;
            if (pedido) {
                // Stock vigente (los pedidos anteriores de esta misma carga ya descontaron).
                Integer stock = jdbc.queryForObject("select stock_actual from productos where id = ?", Integer.class, p.id());
                if (!"DISPONIBLE".equals(p.disponibilidad()) || (stock != null && stock < 30)) continue;
            }
            List<Variante> vs = variantes(jdbc, p.id());
            Variante v = !vs.isEmpty() && random.nextBoolean() ? vs.get(random.nextInt(vs.size())) : null;
            usados.add(p.id());
            out.add(new Linea(p, v, 2 + random.nextInt(14)));
        }
        return out;
    }

    private static BigDecimal total(List<Linea> lineas) {
        BigDecimal total = BigDecimal.ZERO;
        for (Linea l : lineas) {
            if (l.subtotal() == null) return null;
            total = total.add(l.subtotal());
        }
        return total;
    }

    private void insertDetalles(JdbcTemplate jdbc, String tabla, String fk, UUID padre, String codigo, List<Linea> lineas) {
        int n = 0;
        for (Linea l : lineas) {
            String nombre = l.variante() == null ? l.producto().nombre() : l.producto().nombre() + " - " + l.variante().nombre();
            String sku = l.variante() == null ? l.producto().sku() : l.variante().sku();
            jdbc.update("insert into " + tabla + " (id, nombre_producto, sku, cantidad, precio_unitario, subtotal, notas, " + fk + ", producto_id, variante_id) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                    id(tabla, codigo + ":" + n++), nombre, sku, l.cantidad(), l.precioUnitario(), l.subtotal(), null,
                    padre, l.producto().id(), l.variante() == null ? null : l.variante().id());
        }
    }

    private static int indexOf(String[] array, String value) {
        for (int i = 0; i < array.length; i++) if (array[i].equals(value)) return i;
        return 0;
    }
}
