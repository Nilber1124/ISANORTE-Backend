-- Contenido público inicial extraído del frontend estático vigente en la Fase 2B.
-- Migración única PostgreSQL. Requiere exactamente una Empresa y su ConfiguracionSitio 1:1.
-- No borra contenido existente; reconcilia raíces por claves naturales e hijos por padre + orden.

DO $$
DECLARE
    v_empresa_id UUID;
    v_sitio_id UUID;
    v_empresa_count INTEGER;
    v_sitio_count INTEGER;
BEGIN
    SELECT COUNT(*) INTO v_empresa_count FROM empresa;
    IF v_empresa_count <> 1 THEN
        RAISE EXCEPTION 'V4 ISANORTE requiere exactamente una Empresa; se encontraron %', v_empresa_count;
    END IF;

    SELECT id INTO STRICT v_empresa_id FROM empresa;
    SELECT COUNT(*) INTO v_sitio_count
      FROM configuraciones_sitio
     WHERE empresa_id = v_empresa_id;
    IF v_sitio_count <> 1 OR (SELECT COUNT(*) FROM configuraciones_sitio) <> 1 THEN
        RAISE EXCEPTION 'V4 ISANORTE requiere exactamente una ConfiguracionSitio asociada a la única Empresa';
    END IF;
    SELECT id INTO STRICT v_sitio_id
      FROM configuraciones_sitio
     WHERE empresa_id = v_empresa_id;

    UPDATE configuraciones_sitio
       SET clave = 'isanorte',
           titulo_sitio = 'ISANORTE',
           descripcion_sitio = 'Transformamos espacios elevando el nivel de infraestructura, diseño, confort y estética, superando tus expectativas.',
           texto_pie_pagina = '© 2024 ISANORTE. Todos los derechos reservados.',
           fecha_actualizacion = CURRENT_TIMESTAMP
     WHERE id = v_sitio_id;

    UPDATE empresa
       SET resumen_nosotros = CASE
               WHEN NULLIF(BTRIM(resumen_nosotros), '') IS NULL OR resumen_nosotros = 'gfsdgdf'
               THEN 'Transformamos espacios elevando el nivel de infraestructura, diseño, confort y estética, superando tus expectativas.'
               ELSE resumen_nosotros END,
           mision = CASE
               WHEN NULLIF(BTRIM(mision), '') IS NULL OR mision = 'gdfgdfg'
               THEN 'Ejecutar cada proyecto constructivo y arquitectónico con precisión técnica, cumpliendo los plazos pactados y superando las expectativas de nuestros clientes.'
               ELSE mision END,
           vision = CASE
               WHEN NULLIF(BTRIM(vision), '') IS NULL OR vision = 'dgdfgdg'
               THEN 'Ser la constructora de referencia en Ecuador para quienes buscan una obra que combine solidez estructural con un diseño arquitectónico contemporáneo, sin concesiones en ninguna de las dos.'
               ELSE vision END,
           valores = CASE
               WHEN NULLIF(BTRIM(valores), '') IS NULL OR valores = 'fdgdf'
               THEN 'RIGUROSIDAD TÉCNICA | DISEÑO SIN LÍMITES | TRANSPARENCIA TOTAL'
               ELSE valores END,
           fecha_actualizacion = CURRENT_TIMESTAMP
     WHERE id = v_empresa_id;

    -- Secciones legacy incompatibles con la futura Home agregada: se conservan ocultas.
    UPDATE secciones_landing
       SET visible = FALSE,
           fecha_actualizacion = CURRENT_TIMESTAMP
     WHERE configuracion_sitio_id = v_sitio_id
       AND tipo IN ('EMPRESA', 'CONTACTO', 'PERSONALIZADA');

    INSERT INTO secciones_landing
        (id, tipo, etiqueta, titulo, subtitulo, contenido, imagen_url, imagen_alt,
         texto_boton, enlace_boton, orden, visible, configuracion_sitio_id,
         fecha_creacion, fecha_actualizacion)
    SELECT v.id, v.tipo, v.etiqueta, v.titulo, v.subtitulo, v.contenido, v.imagen_url,
           v.imagen_alt, v.texto_boton, v.enlace_boton, v.orden, TRUE, v_sitio_id,
           CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
      FROM (VALUES
        ('21000000-0000-4000-8000-000000000001'::UUID, 'HERO',
         'EMPRESA DE ARQUITECTURA Y CONSTRUCCIÓN', E'Transformamos espacios\nen experiencias',
         'Soluciones profesionales para construcción, obra civil y acabados.', NULL,
         '/images/recorrido-exterior.jpg', NULL, 'SOLICITAR COTIZACIÓN', '#cotizar', 0),
        ('21000000-0000-4000-8000-000000000002'::UUID, 'SERVICIOS',
         'NUESTROS SERVICIOS', E'Soluciones integrales de alta\ningeniería y diseño', NULL, NULL,
         NULL, NULL, 'VER TODOS LOS SERVICIOS', '#', 1),
        ('21000000-0000-4000-8000-000000000003'::UUID, 'UNIDAD_NEGOCIO',
         'NUEVA LÍNEA DE MOBILIARIO A MEDIDA', E'ISADECOR: Espacios que\ninspiran.', NULL, NULL,
         NULL, NULL, 'CONOCE ISADECOR', '#isadecor', 2),
        ('21000000-0000-4000-8000-000000000004'::UUID, 'PROYECTOS',
         'OBRA EN DESTACADO', E'Excelencia entregada en cada\nmetro cuadrado', NULL, NULL,
         NULL, NULL, 'VER TODOS LOS PROYECTOS', '#', 3),
        ('21000000-0000-4000-8000-000000000005'::UUID, 'CTA', NULL,
         '¿Tienes un proyecto en mente?', NULL,
         'Ofrecemos asesoría técnica integral sin costo para la estimación inicial de tu obra o acabados.',
         '/images/asesora-consultoria.jpg', NULL, 'SOLICITAR ASESORÍA GRATUITA', '#contacto', 4)
      ) AS v(id, tipo, etiqueta, titulo, subtitulo, contenido, imagen_url, imagen_alt,
             texto_boton, enlace_boton, orden)
    ON CONFLICT (configuracion_sitio_id, tipo) DO UPDATE SET
        etiqueta = EXCLUDED.etiqueta, titulo = EXCLUDED.titulo,
        subtitulo = EXCLUDED.subtitulo, contenido = EXCLUDED.contenido,
        imagen_url = EXCLUDED.imagen_url, imagen_alt = EXCLUDED.imagen_alt,
        texto_boton = EXCLUDED.texto_boton, enlace_boton = EXCLUDED.enlace_boton,
        orden = EXCLUDED.orden, visible = TRUE, fecha_actualizacion = CURRENT_TIMESTAMP;

    -- Cinco escenas actuales del recorrido cinematográfico.
    UPDATE hero_scenes h
       SET imagen_url = v.imagen_url, alt = v.alt, activo = TRUE,
           fecha_actualizacion = CURRENT_TIMESTAMP
      FROM secciones_landing s,
           (VALUES
             (0, '/images/recorrido-exterior.jpg', NULL::VARCHAR),
             (1, '/images/recorrido-sala.jpg', NULL::VARCHAR),
             (2, '/images/recorrido-cocina.jpg', NULL::VARCHAR),
             (3, '/images/recorrido-bano.jpg', NULL::VARCHAR),
             (4, '/images/recorrido-dormitorio.jpg', NULL::VARCHAR)
           ) AS v(orden, imagen_url, alt)
     WHERE s.configuracion_sitio_id = v_sitio_id AND s.tipo = 'HERO'
       AND h.seccion_landing_id = s.id AND h.orden = v.orden;

    INSERT INTO hero_scenes
        (id, imagen_url, alt, orden, activo, seccion_landing_id, fecha_creacion, fecha_actualizacion)
    SELECT v.id, v.imagen_url, v.alt, v.orden, TRUE, s.id, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
      FROM secciones_landing s
      CROSS JOIN (VALUES
        ('22000000-0000-4000-8000-000000000001'::UUID, 0, '/images/recorrido-exterior.jpg', NULL::VARCHAR),
        ('22000000-0000-4000-8000-000000000002'::UUID, 1, '/images/recorrido-sala.jpg', NULL::VARCHAR),
        ('22000000-0000-4000-8000-000000000003'::UUID, 2, '/images/recorrido-cocina.jpg', NULL::VARCHAR),
        ('22000000-0000-4000-8000-000000000004'::UUID, 3, '/images/recorrido-bano.jpg', NULL::VARCHAR),
        ('22000000-0000-4000-8000-000000000005'::UUID, 4, '/images/recorrido-dormitorio.jpg', NULL::VARCHAR)
      ) AS v(id, orden, imagen_url, alt)
     WHERE s.configuracion_sitio_id = v_sitio_id AND s.tipo = 'HERO'
       AND NOT EXISTS (SELECT 1 FROM hero_scenes h WHERE h.seccion_landing_id = s.id AND h.orden = v.orden);

    -- Acciones actuales. Los fragmentos se conservan literalmente; algunos no tienen destino aún en la Home.
    UPDATE acciones_landing a
       SET texto = v.texto, enlace = v.enlace, activo = TRUE,
           fecha_actualizacion = CURRENT_TIMESTAMP
      FROM secciones_landing s,
           (VALUES
             ('HERO', 0, 'SOLICITAR COTIZACIÓN', '#cotizar'),
             ('HERO', 1, 'HABLA CON UN ASESOR', '#contacto'),
             ('SERVICIOS', 0, 'VER TODOS LOS SERVICIOS', '#'),
             ('UNIDAD_NEGOCIO', 0, 'CONOCE ISADECOR', '#isadecor'),
             ('UNIDAD_NEGOCIO', 1, 'DESCARGAR CATÁLOGO (PDF)', '#catalogo'),
             ('PROYECTOS', 0, 'VER TODOS LOS PROYECTOS', '#'),
             ('CTA', 0, 'SOLICITAR ASESORÍA GRATUITA', '#contacto')
           ) AS v(tipo, orden, texto, enlace)
     WHERE s.configuracion_sitio_id = v_sitio_id AND s.tipo = v.tipo
       AND a.seccion_landing_id = s.id AND a.orden = v.orden;

    INSERT INTO acciones_landing
        (id, texto, enlace, orden, activo, seccion_landing_id, fecha_creacion, fecha_actualizacion)
    SELECT v.id, v.texto, v.enlace, v.orden, TRUE, s.id, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
      FROM secciones_landing s
      JOIN (VALUES
        ('23000000-0000-4000-8000-000000000001'::UUID, 'HERO', 0, 'SOLICITAR COTIZACIÓN', '#cotizar'),
        ('23000000-0000-4000-8000-000000000002'::UUID, 'HERO', 1, 'HABLA CON UN ASESOR', '#contacto'),
        ('23000000-0000-4000-8000-000000000003'::UUID, 'SERVICIOS', 0, 'VER TODOS LOS SERVICIOS', '#'),
        ('23000000-0000-4000-8000-000000000004'::UUID, 'UNIDAD_NEGOCIO', 0, 'CONOCE ISADECOR', '#isadecor'),
        ('23000000-0000-4000-8000-000000000005'::UUID, 'UNIDAD_NEGOCIO', 1, 'DESCARGAR CATÁLOGO (PDF)', '#catalogo'),
        ('23000000-0000-4000-8000-000000000006'::UUID, 'PROYECTOS', 0, 'VER TODOS LOS PROYECTOS', '#'),
        ('23000000-0000-4000-8000-000000000007'::UUID, 'CTA', 0, 'SOLICITAR ASESORÍA GRATUITA', '#contacto')
      ) AS v(id, tipo, orden, texto, enlace) ON s.tipo = v.tipo
     WHERE s.configuracion_sitio_id = v_sitio_id
       AND NOT EXISTS (SELECT 1 FROM acciones_landing a WHERE a.seccion_landing_id = s.id AND a.orden = v.orden);

    -- Servicios: detalle aprobado de /servicios; resumen de las cards Home.
    INSERT INTO servicios
        (id, nombre, slug, resumen, descripcion, icono, imagen_url, etiqueta, imagen_alt,
         activo, destacado, orden, fecha_creacion, fecha_actualizacion)
    SELECT v.id, v.nombre, v.slug, v.resumen, v.descripcion, NULL, v.imagen_url, v.etiqueta,
           v.imagen_alt, TRUE, TRUE, v.orden, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
      FROM (VALUES
        ('24000000-0000-4000-8000-000000000001'::UUID, 'Construcción Residencial & Comercial', 'construccion',
         'Estructuras de hormigón armado, edificaciones comerciales e industriales con los más altos estándares.',
         'Ejecutamos obra civil de cualquier escala. Desde la cimentación estructural hasta la entrega de llave en mano de edificios corporativos y residencias de lujo.',
         '/images/servicios-construccion.jpg', 'CONSTRUCCIÓN', 'Estructura de construcción residencial y comercial en obra', 0),
        ('24000000-0000-4000-8000-000000000002'::UUID, 'Acabados Premium & Revestimientos Técnicos', 'acabados',
         'Instalación de porcelanatos, mármol, madera y microcemento con acabados de primera calidad.',
         'Instalamos materiales de última tecnología para interiores y exteriores. Especialistas en piso SPC, mármol de alta densidad, revestimientos vinílicos y acústicos.',
         '/images/servicios-acabados.jpg', 'ACABADOS', 'Acabados premium y revestimientos técnicos de interiores', 1),
        ('24000000-0000-4000-8000-000000000003'::UUID, 'Decoración & Diseño Interior Inmersivo', 'decoracion',
         'Creación de espacios funcionales y estéticos que reflejan tu estilo y personalidad.',
         'Conceptualizamos atmósferas que reflejan sofisticación. Coordinación de ebanistería técnica, cielos rasos acústicos, y mobiliario personalizado de madera y metal.',
         '/images/servicios-decoracion.jpg', 'DECORACIÓN', 'Diseño interior inmersivo con mobiliario personalizado', 2)
      ) AS v(id, nombre, slug, resumen, descripcion, imagen_url, etiqueta, imagen_alt, orden)
    ON CONFLICT (slug) DO UPDATE SET
        nombre = EXCLUDED.nombre, resumen = EXCLUDED.resumen, descripcion = EXCLUDED.descripcion,
        imagen_url = EXCLUDED.imagen_url, etiqueta = EXCLUDED.etiqueta,
        imagen_alt = EXCLUDED.imagen_alt, activo = TRUE, destacado = TRUE,
        orden = EXCLUDED.orden, fecha_actualizacion = CURRENT_TIMESTAMP;

    UPDATE beneficios_servicio b
       SET texto = v.texto, activo = TRUE, fecha_actualizacion = CURRENT_TIMESTAMP
      FROM servicios s,
           (VALUES
             ('construccion', 0, 'Estudios de suelo & cimentación'),
             ('construccion', 1, 'Estructuras de concreto reforzado & acero'),
             ('construccion', 2, 'Sistemas bioclimáticos avanzados'),
             ('acabados', 0, 'Instalación técnica de mármol SPC'),
             ('acabados', 1, 'Piso de madera ingeniería de alto tránsito'),
             ('acabados', 2, 'Pintura y estucos de alta calidad arquitectónica'),
             ('decoracion', 0, 'Sistemas de iluminación LED indirecta integrada'),
             ('decoracion', 1, 'Wall panels acústicos de importación'),
             ('decoracion', 2, 'Planos de distribución 3D fotorealistas')
           ) AS v(slug, orden, texto)
     WHERE s.slug = v.slug AND b.servicio_id = s.id AND b.orden = v.orden;

    INSERT INTO beneficios_servicio
        (id, texto, orden, activo, servicio_id, fecha_creacion, fecha_actualizacion)
    SELECT v.id, v.texto, v.orden, TRUE, s.id, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
      FROM servicios s
      JOIN (VALUES
        ('25000000-0000-4000-8000-000000000001'::UUID, 'construccion', 0, 'Estudios de suelo & cimentación'),
        ('25000000-0000-4000-8000-000000000002'::UUID, 'construccion', 1, 'Estructuras de concreto reforzado & acero'),
        ('25000000-0000-4000-8000-000000000003'::UUID, 'construccion', 2, 'Sistemas bioclimáticos avanzados'),
        ('25000000-0000-4000-8000-000000000004'::UUID, 'acabados', 0, 'Instalación técnica de mármol SPC'),
        ('25000000-0000-4000-8000-000000000005'::UUID, 'acabados', 1, 'Piso de madera ingeniería de alto tránsito'),
        ('25000000-0000-4000-8000-000000000006'::UUID, 'acabados', 2, 'Pintura y estucos de alta calidad arquitectónica'),
        ('25000000-0000-4000-8000-000000000007'::UUID, 'decoracion', 0, 'Sistemas de iluminación LED indirecta integrada'),
        ('25000000-0000-4000-8000-000000000008'::UUID, 'decoracion', 1, 'Wall panels acústicos de importación'),
        ('25000000-0000-4000-8000-000000000009'::UUID, 'decoracion', 2, 'Planos de distribución 3D fotorealistas')
      ) AS v(id, slug, orden, texto) ON s.slug = v.slug
     WHERE NOT EXISTS (SELECT 1 FROM beneficios_servicio b WHERE b.servicio_id = s.id AND b.orden = v.orden);

    -- Unidad destacada ISADECOR. No se crea recurso CATALOGO porque no existe PDF real.
    IF EXISTS (SELECT 1 FROM unidades_negocio
                WHERE empresa_id = v_empresa_id AND activo = TRUE AND destacado = TRUE
                  AND slug <> 'isadecor') THEN
        RAISE EXCEPTION 'V4 ISANORTE no puede destacar ISADECOR: ya existe otra unidad activa destacada';
    END IF;

    INSERT INTO unidades_negocio
        (id, nombre, slug, descripcion, icono, imagen_url, imagen_alt, activo, destacado,
         orden, empresa_id, fecha_creacion, fecha_actualizacion)
    VALUES
        ('26000000-0000-4000-8000-000000000001'::UUID, 'ISADECOR', 'isadecor',
         'Diseño interior y mobiliario a medida. Creamos atmósferas únicas que reflejan la esencia de quienes las habitan, uniendo materiales premium con estética atemporal.',
         NULL, '/images/isadecor-fondo.jpg', NULL, TRUE, TRUE,
         0, v_empresa_id, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
    ON CONFLICT (slug) DO UPDATE SET
        nombre = EXCLUDED.nombre, descripcion = EXCLUDED.descripcion,
        imagen_url = EXCLUDED.imagen_url, imagen_alt = EXCLUDED.imagen_alt,
        activo = TRUE, destacado = TRUE, orden = EXCLUDED.orden,
        fecha_actualizacion = CURRENT_TIMESTAMP;

    UPDATE recursos_unidad_negocio r
       SET url = v.url, alt = v.alt, etiqueta = NULL, activo = TRUE,
           fecha_actualizacion = CURRENT_TIMESTAMP
      FROM unidades_negocio u,
           (VALUES
             ('IMAGEN_FONDO', 0, '/images/isadecor-fondo.jpg', NULL::VARCHAR),
             ('IMAGEN_EDITORIAL', 1, '/images/isadecor-top.jpg', 'ISADECOR: Espacios que inspiran.'),
             ('IMAGEN_EDITORIAL', 2, '/images/isadecor-left-1.jpg', 'ISADECOR: Espacios que inspiran.'),
             ('IMAGEN_EDITORIAL', 3, '/images/isadecor-left-2.jpg', 'ISADECOR: Espacios que inspiran.')
           ) AS v(tipo, orden, url, alt)
     WHERE u.slug = 'isadecor' AND r.unidad_negocio_id = u.id
       AND r.tipo = v.tipo AND r.orden = v.orden;

    INSERT INTO recursos_unidad_negocio
        (id, tipo, url, alt, etiqueta, orden, activo, unidad_negocio_id, fecha_creacion, fecha_actualizacion)
    SELECT v.id, v.tipo, v.url, v.alt, NULL, v.orden, TRUE, u.id, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
      FROM unidades_negocio u
      CROSS JOIN (VALUES
        ('27000000-0000-4000-8000-000000000001'::UUID, 'IMAGEN_FONDO', 0, '/images/isadecor-fondo.jpg', NULL::VARCHAR),
        ('27000000-0000-4000-8000-000000000002'::UUID, 'IMAGEN_EDITORIAL', 1, '/images/isadecor-top.jpg', 'ISADECOR: Espacios que inspiran.'),
        ('27000000-0000-4000-8000-000000000003'::UUID, 'IMAGEN_EDITORIAL', 2, '/images/isadecor-left-1.jpg', 'ISADECOR: Espacios que inspiran.'),
        ('27000000-0000-4000-8000-000000000004'::UUID, 'IMAGEN_EDITORIAL', 3, '/images/isadecor-left-2.jpg', 'ISADECOR: Espacios que inspiran.')
      ) AS v(id, tipo, orden, url, alt)
     WHERE u.slug = 'isadecor'
       AND NOT EXISTS (SELECT 1 FROM recursos_unidad_negocio r
                        WHERE r.unidad_negocio_id = u.id AND r.tipo = v.tipo AND r.orden = v.orden);

    -- Proyectos de Home y /proyectos. El slug es la clave natural estable.
    INSERT INTO proyectos
        (id, nombre, slug, cliente, ubicacion, fecha_proyecto, descripcion,
         destacado, activo, orden, fecha_creacion, fecha_actualizacion)
    SELECT v.id, v.nombre, v.slug, NULL, v.ubicacion, v.fecha_proyecto, v.descripcion,
           v.destacado, TRUE, v.orden, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
      FROM (VALUES
        ('28000000-0000-4000-8000-000000000001'::UUID, 'Residencia Aura', 'residencia-aura', 'Valle de los Chillos, Quito', 'Proyecto 2024', '', TRUE, 0),
        ('28000000-0000-4000-8000-000000000002'::UUID, 'Edificio Tech-Corporate', 'edificio-tech-corporate', 'Sector Financiero, Quito', 'Proyecto 2023', '', TRUE, 1),
        ('28000000-0000-4000-8000-000000000003'::UUID, 'Villa Lomas Alta', 'villa-lomas-alta', 'Bogotá, CO', NULL, 'Estructura expuesta de cipromio y persianas de control.', FALSE, 2),
        ('28000000-0000-4000-8000-000000000004'::UUID, 'Penthouse Abisal', 'penthouse-abisal', 'Medellín, CO', NULL, 'Interiorismo residencial de lujo.', FALSE, 3),
        ('28000000-0000-4000-8000-000000000005'::UUID, 'Oficinas Nexus', 'oficinas-nexus', NULL, NULL, 'Torre corporativa', FALSE, 4),
        ('28000000-0000-4000-8000-000000000006'::UUID, 'Clínica Sanitas Norte', 'clinica-sanitas-norte', NULL, NULL, 'Infraestructura de salud', FALSE, 5),
        ('28000000-0000-4000-8000-000000000007'::UUID, 'Showroom ISADECOR', 'showroom-isadecor', NULL, NULL, 'Diseño de interiores', FALSE, 6)
      ) AS v(id, nombre, slug, ubicacion, fecha_proyecto, descripcion, destacado, orden)
    ON CONFLICT (slug) DO UPDATE SET
        nombre = EXCLUDED.nombre, ubicacion = EXCLUDED.ubicacion,
        fecha_proyecto = EXCLUDED.fecha_proyecto, descripcion = EXCLUDED.descripcion,
        destacado = EXCLUDED.destacado, activo = TRUE, orden = EXCLUDED.orden,
        fecha_actualizacion = CURRENT_TIMESTAMP;

    UPDATE imagenes_proyecto i
       SET url = v.url, titulo = v.titulo, descripcion = NULL, alt = v.alt,
           tipo = 'GENERAL', es_principal = TRUE
      FROM proyectos p,
           (VALUES
             ('residencia-aura', '/images/residencia-aura.jpg', 'Residencia Aura', NULL::VARCHAR),
             ('edificio-tech-corporate', 'https://images.unsplash.com/photo-1486406146926-c627a92ad1ab?q=80&w=1200&auto=format&fit=crop', 'Edificio Tech-Corporate', NULL::VARCHAR),
             ('villa-lomas-alta', '/images/proyectos-villa-lomas-alta.jpg', 'Villa Lomas Alta', 'Villa residencial de estructura expuesta en Lomas Altas'),
             ('penthouse-abisal', '/images/proyectos-penthouse-abisal.jpg', 'Penthouse Abisal', 'Interior de penthouse de lujo con acabados premium'),
             ('oficinas-nexus', '/images/proyectos-oficinas-nexus.jpg', 'Oficinas Nexus', 'Torre corporativa de oficinas Nexus'),
             ('clinica-sanitas-norte', '/images/proyectos-clinica-sanitas-norte.jpg', 'Clínica Sanitas Norte', 'Infraestructura hospitalaria de la Clínica Sanitas Norte'),
             ('showroom-isadecor', '/images/proyectos-showroom-isadecor.jpg', 'Showroom ISADECOR', 'Showroom de diseño de interiores de ISADECOR')
           ) AS v(slug, url, titulo, alt)
     WHERE p.slug = v.slug AND i.proyecto_id = p.id AND i.orden = 0;

    INSERT INTO imagenes_proyecto
        (id, url, titulo, descripcion, alt, tipo, es_principal, orden, proyecto_id, fecha_creacion)
    SELECT v.id, v.url, v.titulo, NULL, v.alt, 'GENERAL', TRUE, 0, p.id, CURRENT_TIMESTAMP
      FROM proyectos p
      JOIN (VALUES
        ('29000000-0000-4000-8000-000000000001'::UUID, 'residencia-aura', '/images/residencia-aura.jpg', 'Residencia Aura', NULL::VARCHAR),
        ('29000000-0000-4000-8000-000000000002'::UUID, 'edificio-tech-corporate', 'https://images.unsplash.com/photo-1486406146926-c627a92ad1ab?q=80&w=1200&auto=format&fit=crop', 'Edificio Tech-Corporate', NULL::VARCHAR),
        ('29000000-0000-4000-8000-000000000003'::UUID, 'villa-lomas-alta', '/images/proyectos-villa-lomas-alta.jpg', 'Villa Lomas Alta', 'Villa residencial de estructura expuesta en Lomas Altas'),
        ('29000000-0000-4000-8000-000000000004'::UUID, 'penthouse-abisal', '/images/proyectos-penthouse-abisal.jpg', 'Penthouse Abisal', 'Interior de penthouse de lujo con acabados premium'),
        ('29000000-0000-4000-8000-000000000005'::UUID, 'oficinas-nexus', '/images/proyectos-oficinas-nexus.jpg', 'Oficinas Nexus', 'Torre corporativa de oficinas Nexus'),
        ('29000000-0000-4000-8000-000000000006'::UUID, 'clinica-sanitas-norte', '/images/proyectos-clinica-sanitas-norte.jpg', 'Clínica Sanitas Norte', 'Infraestructura hospitalaria de la Clínica Sanitas Norte'),
        ('29000000-0000-4000-8000-000000000007'::UUID, 'showroom-isadecor', '/images/proyectos-showroom-isadecor.jpg', 'Showroom ISADECOR', 'Showroom de diseño de interiores de ISADECOR')
      ) AS v(id, slug, url, titulo, alt) ON p.slug = v.slug
     WHERE NOT EXISTS (SELECT 1 FROM imagenes_proyecto i WHERE i.proyecto_id = p.id AND i.orden = 0);

    INSERT INTO proyecto_servicio (proyecto_id, servicio_id)
    SELECT p.id, s.id
      FROM (VALUES
        ('villa-lomas-alta', 'construccion'),
        ('penthouse-abisal', 'decoracion'),
        ('clinica-sanitas-norte', 'construccion'),
        ('showroom-isadecor', 'decoracion')
      ) AS v(proyecto_slug, servicio_slug)
      JOIN proyectos p ON p.slug = v.proyecto_slug
      JOIN servicios s ON s.slug = v.servicio_slug
    ON CONFLICT (proyecto_id, servicio_id) DO NOTHING;

    -- Estadísticas visibles de Nosotros.
    UPDATE estadisticas_empresa e
       SET valor = v.valor, prefijo = v.prefijo, sufijo = v.sufijo,
           etiqueta = v.etiqueta, activo = TRUE, fecha_actualizacion = CURRENT_TIMESTAMP
      FROM (VALUES
        (0, 120.00, '+', NULL::VARCHAR, 'PROYECTOS ENTREGADOS'),
        (1, 15.00, '+', NULL::VARCHAR, 'AÑOS DE EXPERIENCIA'),
        (2, 98.00, NULL::VARCHAR, '%', 'CLIENTES SATISFECHOS'),
        (3, 100.00, NULL::VARCHAR, '%', 'DISEÑO A MEDIDA')
      ) AS v(orden, valor, prefijo, sufijo, etiqueta)
     WHERE e.empresa_id = v_empresa_id AND e.orden = v.orden;

    INSERT INTO estadisticas_empresa
        (id, valor, prefijo, sufijo, etiqueta, orden, activo, empresa_id, fecha_creacion, fecha_actualizacion)
    SELECT v.id, v.valor, v.prefijo, v.sufijo, v.etiqueta, v.orden, TRUE,
           v_empresa_id, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
      FROM (VALUES
        ('2a000000-0000-4000-8000-000000000001'::UUID, 0, 120.00, '+', NULL::VARCHAR, 'PROYECTOS ENTREGADOS'),
        ('2a000000-0000-4000-8000-000000000002'::UUID, 1, 15.00, '+', NULL::VARCHAR, 'AÑOS DE EXPERIENCIA'),
        ('2a000000-0000-4000-8000-000000000003'::UUID, 2, 98.00, NULL::VARCHAR, '%', 'CLIENTES SATISFECHOS'),
        ('2a000000-0000-4000-8000-000000000004'::UUID, 3, 100.00, NULL::VARCHAR, '%', 'DISEÑO A MEDIDA')
      ) AS v(id, orden, valor, prefijo, sufijo, etiqueta)
     WHERE NOT EXISTS (SELECT 1 FROM estadisticas_empresa e
                        WHERE e.empresa_id = v_empresa_id AND e.orden = v.orden);

    -- Editorial de páginas. No duplica colecciones de servicios/proyectos ni datos de contacto.
    INSERT INTO contenidos_pagina
        (id, pagina, eyebrow, titulo, introduccion, descripcion, imagen_url, imagen_alt,
         imagen_fondo_url, activo, configuracion_sitio_id, fecha_creacion, fecha_actualizacion)
    SELECT v.id, v.pagina, v.eyebrow, v.titulo, v.introduccion, v.descripcion,
           v.imagen_url, v.imagen_alt, v.imagen_fondo_url, TRUE, v_sitio_id,
           CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
      FROM (VALUES
        ('2b000000-0000-4000-8000-000000000001'::UUID, 'NOSOTROS', 'NOSOTROS — IDENTIDAD',
         E'Transformamos espacios\nen experiencias.',
         'Soluciones profesionales para construcción, obra civil y acabados.',
         'Transformamos espacios elevando el nivel de infraestructura, diseño, confort y estética, superando tus expectativas.',
         '/images/nosotros-hero.jpg', NULL::VARCHAR, '/images/isadecor-fondo.jpg'),
        ('2b000000-0000-4000-8000-000000000002'::UUID, 'SERVICIOS', 'PORTAFOLIO // LO QUE HACEMOS',
         'Servicios técnicos especializados de diseño y construcción.', NULL, NULL, NULL, NULL, NULL),
        ('2b000000-0000-4000-8000-000000000003'::UUID, 'PROYECTOS', 'NUESTRAS REFERENCIAS // OBRAS INSIGNIA',
         'Galería Editorial de Proyectos', NULL, NULL, NULL, NULL, NULL),
        ('2b000000-0000-4000-8000-000000000004'::UUID, 'CONTACTO', 'CONTACTO DIRECTO // HABLEMOS',
         'Conectemos con tu próximo proyecto', NULL,
         'Nuestro equipo técnico e ingenieros están disponibles para agendar visitas a obra o responder cotizaciones detalladas de manera rápida.',
         NULL, NULL, NULL)
      ) AS v(id, pagina, eyebrow, titulo, introduccion, descripcion, imagen_url, imagen_alt, imagen_fondo_url)
    ON CONFLICT (configuracion_sitio_id, pagina) DO UPDATE SET
        eyebrow = EXCLUDED.eyebrow, titulo = EXCLUDED.titulo,
        introduccion = EXCLUDED.introduccion, descripcion = EXCLUDED.descripcion,
        imagen_url = EXCLUDED.imagen_url, imagen_alt = EXCLUDED.imagen_alt,
        imagen_fondo_url = EXCLUDED.imagen_fondo_url, activo = TRUE,
        fecha_actualizacion = CURRENT_TIMESTAMP;

    -- Tags exactos y ordenados de la página Nosotros. Solo se reconcilian las posiciones objetivo.
    UPDATE contenido_pagina_tags t
       SET tag = v.tag
      FROM contenidos_pagina c,
           (VALUES
             (0, 'ARQUITECTURA'), (1, 'CONSTRUCCIÓN'), (2, 'OBRA CIVIL'), (3, 'ACABADOS')
           ) AS v(orden, tag)
     WHERE c.configuracion_sitio_id = v_sitio_id AND c.pagina = 'NOSOTROS'
       AND t.contenido_pagina_id = c.id AND t.orden = v.orden;

    INSERT INTO contenido_pagina_tags (contenido_pagina_id, orden, tag)
    SELECT c.id, v.orden, v.tag
      FROM contenidos_pagina c
      CROSS JOIN (VALUES
        (0, 'ARQUITECTURA'), (1, 'CONSTRUCCIÓN'), (2, 'OBRA CIVIL'), (3, 'ACABADOS')
      ) AS v(orden, tag)
     WHERE c.configuracion_sitio_id = v_sitio_id AND c.pagina = 'NOSOTROS'
       AND NOT EXISTS (SELECT 1 FROM contenido_pagina_tags t
                        WHERE t.contenido_pagina_id = c.id AND t.orden = v.orden);

    -- SEO mínimo derivado exclusivamente de títulos/descripciones ya presentes.
    UPDATE seo_paginas seo
       SET title = v.title, description = v.description, og_image_url = v.og_image_url,
           robots = 'INDEX_FOLLOW', fecha_actualizacion = CURRENT_TIMESTAMP
      FROM (VALUES
        ('HOME', 'ISANORTE', 'Transformamos espacios elevando el nivel de infraestructura, diseño, confort y estética, superando tus expectativas.', '/images/recorrido-exterior.jpg'),
        ('NOSOTROS', E'Transformamos espacios\nen experiencias.', 'Soluciones profesionales para construcción, obra civil y acabados.', '/images/nosotros-hero.jpg'),
        ('SERVICIOS', 'Servicios técnicos especializados de diseño y construcción.', 'Servicios técnicos especializados de diseño y construcción.', NULL::VARCHAR),
        ('PROYECTOS', 'Galería Editorial de Proyectos', 'Galería Editorial de Proyectos', NULL::VARCHAR),
        ('CONTACTO', 'Conectemos con tu próximo proyecto', 'Nuestro equipo técnico e ingenieros están disponibles para agendar visitas a obra o responder cotizaciones detalladas de manera rápida.', NULL::VARCHAR)
      ) AS v(tipo_pagina, title, description, og_image_url)
     WHERE seo.configuracion_sitio_id = v_sitio_id
       AND seo.tipo_pagina = v.tipo_pagina AND seo.unidad_negocio_id IS NULL;

    INSERT INTO seo_paginas
        (id, tipo_pagina, title, description, og_image_url, robots,
         configuracion_sitio_id, unidad_negocio_id, fecha_creacion, fecha_actualizacion)
    SELECT v.id, v.tipo_pagina, v.title, v.description, v.og_image_url, 'INDEX_FOLLOW',
           v_sitio_id, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
      FROM (VALUES
        ('2c000000-0000-4000-8000-000000000001'::UUID, 'HOME', 'ISANORTE', 'Transformamos espacios elevando el nivel de infraestructura, diseño, confort y estética, superando tus expectativas.', '/images/recorrido-exterior.jpg'),
        ('2c000000-0000-4000-8000-000000000002'::UUID, 'NOSOTROS', E'Transformamos espacios\nen experiencias.', 'Soluciones profesionales para construcción, obra civil y acabados.', '/images/nosotros-hero.jpg'),
        ('2c000000-0000-4000-8000-000000000003'::UUID, 'SERVICIOS', 'Servicios técnicos especializados de diseño y construcción.', 'Servicios técnicos especializados de diseño y construcción.', NULL::VARCHAR),
        ('2c000000-0000-4000-8000-000000000004'::UUID, 'PROYECTOS', 'Galería Editorial de Proyectos', 'Galería Editorial de Proyectos', NULL::VARCHAR),
        ('2c000000-0000-4000-8000-000000000005'::UUID, 'CONTACTO', 'Conectemos con tu próximo proyecto', 'Nuestro equipo técnico e ingenieros están disponibles para agendar visitas a obra o responder cotizaciones detalladas de manera rápida.', NULL::VARCHAR)
      ) AS v(id, tipo_pagina, title, description, og_image_url)
     WHERE NOT EXISTS (SELECT 1 FROM seo_paginas seo
                        WHERE seo.configuracion_sitio_id = v_sitio_id
                          AND seo.tipo_pagina = v.tipo_pagina
                          AND seo.unidad_negocio_id IS NULL);

    UPDATE seo_paginas seo
       SET title = 'ISADECOR',
           description = 'Diseño interior y mobiliario a medida. Creamos atmósferas únicas que reflejan la esencia de quienes las habitan, uniendo materiales premium con estética atemporal.',
           og_image_url = '/images/isadecor-fondo.jpg', robots = 'INDEX_FOLLOW',
           configuracion_sitio_id = v_sitio_id, fecha_actualizacion = CURRENT_TIMESTAMP
      FROM unidades_negocio u
     WHERE u.slug = 'isadecor' AND seo.tipo_pagina = 'UNIDAD_NEGOCIO'
       AND seo.unidad_negocio_id = u.id;

    INSERT INTO seo_paginas
        (id, tipo_pagina, title, description, og_image_url, robots,
         configuracion_sitio_id, unidad_negocio_id, fecha_creacion, fecha_actualizacion)
    SELECT '2c000000-0000-4000-8000-000000000006'::UUID, 'UNIDAD_NEGOCIO', 'ISADECOR',
           'Diseño interior y mobiliario a medida. Creamos atmósferas únicas que reflejan la esencia de quienes las habitan, uniendo materiales premium con estética atemporal.',
           '/images/isadecor-fondo.jpg', 'INDEX_FOLLOW', v_sitio_id, u.id,
           CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
      FROM unidades_negocio u
     WHERE u.slug = 'isadecor'
       AND NOT EXISTS (SELECT 1 FROM seo_paginas seo WHERE seo.unidad_negocio_id = u.id);
END $$;
