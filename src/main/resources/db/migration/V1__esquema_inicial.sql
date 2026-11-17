-- Esquema inicial de ISANORTE (constructora-api).
-- Es portable: lo ejecutan PostgreSQL (producción y desarrollo) y H2 en modo PostgreSQL (pruebas).
-- Lo exclusivo de PostgreSQL (índices parciales) está en V2 y los datos mínimos de arranque en V3.
-- Las tablas de contenido nuevas (preguntas frecuentes, equipo, credenciales, etc.) se agregan al final.

-- ---- Tablas base ----
-- Baseline del esquema existente antes de incorporar Flyway.
-- En una instalación existente se registra manualmente la versión 1 y este script no se ejecuta.
-- En una instalación nueva Flyway lo aplica para construir el esquema histórico completo.

CREATE TABLE empresa (
    id UUID PRIMARY KEY,
    razon_social VARCHAR(180) NOT NULL,
    nombre_comercial VARCHAR(180) NOT NULL,
    ruc VARCHAR(20) NOT NULL,
    direccion VARCHAR(255),
    ciudad VARCHAR(100),
    telefono VARCHAR(30),
    telefono_secundario VARCHAR(30),
    email VARCHAR(120),
    email_ventas VARCHAR(120),
    whatsapp VARCHAR(30),
    horario_atencion VARCHAR(180),
    mision TEXT,
    vision TEXT,
    valores TEXT,
    resumen_nosotros TEXT,
    fecha_creacion TIMESTAMP(6) NOT NULL,
    fecha_actualizacion TIMESTAMP(6) NOT NULL,
    CONSTRAINT uk_empresa_ruc UNIQUE (ruc)
);

CREATE TABLE configuraciones_sitio (
    id UUID PRIMARY KEY,
    titulo_sitio VARCHAR(150),
    descripcion_sitio VARCHAR(300),
    logo_url VARCHAR(500),
    logo_blanco_url VARCHAR(500),
    favicon_url VARCHAR(500),
    color_primario VARCHAR(20),
    color_secundario VARCHAR(20),
    texto_pie_pagina VARCHAR(255),
    scripts_head TEXT,
    scripts_body TEXT,
    empresa_id UUID NOT NULL,
    fecha_actualizacion TIMESTAMP(6) NOT NULL,
    CONSTRAINT uk_configuracion_sitio_empresa UNIQUE (empresa_id),
    CONSTRAINT fk_configuracion_sitio_empresa FOREIGN KEY (empresa_id) REFERENCES empresa(id)
);

CREATE TABLE secciones_landing (
    id UUID PRIMARY KEY,
    tipo VARCHAR(50) NOT NULL,
    titulo VARCHAR(180),
    subtitulo VARCHAR(255),
    contenido TEXT,
    imagen_url VARCHAR(500),
    texto_boton VARCHAR(80),
    enlace_boton VARCHAR(300),
    orden INTEGER NOT NULL,
    visible BOOLEAN NOT NULL,
    configuracion_sitio_id UUID NOT NULL,
    fecha_creacion TIMESTAMP(6) NOT NULL,
    fecha_actualizacion TIMESTAMP(6) NOT NULL,
    CONSTRAINT fk_seccion_landing_configuracion FOREIGN KEY (configuracion_sitio_id)
        REFERENCES configuraciones_sitio(id)
);

CREATE TABLE redes_sociales (
    id UUID PRIMARY KEY,
    nombre VARCHAR(60) NOT NULL,
    url VARCHAR(300) NOT NULL,
    icono VARCHAR(60),
    orden INTEGER NOT NULL,
    activo BOOLEAN NOT NULL,
    empresa_id UUID NOT NULL,
    fecha_creacion TIMESTAMP(6) NOT NULL,
    fecha_actualizacion TIMESTAMP(6) NOT NULL,
    CONSTRAINT fk_red_social_empresa FOREIGN KEY (empresa_id) REFERENCES empresa(id)
);

CREATE TABLE unidades_negocio (
    id UUID PRIMARY KEY,
    nombre VARCHAR(120) NOT NULL,
    slug VARCHAR(150) NOT NULL,
    descripcion TEXT,
    icono VARCHAR(100),
    imagen_url VARCHAR(500),
    activo BOOLEAN NOT NULL,
    orden INTEGER NOT NULL,
    empresa_id UUID NOT NULL,
    fecha_creacion TIMESTAMP(6) NOT NULL,
    fecha_actualizacion TIMESTAMP(6) NOT NULL,
    CONSTRAINT uk_unidad_negocio_nombre UNIQUE (nombre),
    CONSTRAINT uk_unidad_negocio_slug UNIQUE (slug),
    CONSTRAINT fk_unidad_negocio_empresa FOREIGN KEY (empresa_id) REFERENCES empresa(id)
);

CREATE TABLE categorias_producto (
    id UUID PRIMARY KEY,
    nombre VARCHAR(120) NOT NULL,
    slug VARCHAR(150) NOT NULL,
    descripcion TEXT,
    imagen_url VARCHAR(500),
    activo BOOLEAN NOT NULL,
    orden INTEGER NOT NULL,
    unidad_negocio_id UUID,
    fecha_creacion TIMESTAMP(6) NOT NULL,
    fecha_actualizacion TIMESTAMP(6) NOT NULL,
    CONSTRAINT uk_categoria_producto_slug UNIQUE (slug),
    CONSTRAINT fk_categoria_unidad FOREIGN KEY (unidad_negocio_id) REFERENCES unidades_negocio(id)
);

CREATE TABLE servicios (
    id UUID PRIMARY KEY,
    nombre VARCHAR(180) NOT NULL,
    slug VARCHAR(200) NOT NULL,
    resumen VARCHAR(500),
    descripcion TEXT NOT NULL,
    imagen_url VARCHAR(500),
    activo BOOLEAN NOT NULL,
    destacado BOOLEAN NOT NULL,
    orden INTEGER NOT NULL,
    fecha_creacion TIMESTAMP(6) NOT NULL,
    fecha_actualizacion TIMESTAMP(6) NOT NULL,
    CONSTRAINT uk_servicio_slug UNIQUE (slug)
);

CREATE TABLE proyectos (
    id UUID PRIMARY KEY,
    nombre VARCHAR(180) NOT NULL,
    slug VARCHAR(200) NOT NULL,
    cliente VARCHAR(150),
    ubicacion VARCHAR(180),
    fecha_proyecto VARCHAR(60),
    descripcion TEXT NOT NULL,
    destacado BOOLEAN NOT NULL,
    activo BOOLEAN NOT NULL,
    fecha_creacion TIMESTAMP(6) NOT NULL,
    fecha_actualizacion TIMESTAMP(6) NOT NULL,
    CONSTRAINT uk_proyecto_slug UNIQUE (slug)
);


CREATE TABLE proyecto_servicio (
    proyecto_id UUID NOT NULL,
    servicio_id UUID NOT NULL,
    PRIMARY KEY (proyecto_id, servicio_id),
    CONSTRAINT fk_proyecto_servicio_proyecto FOREIGN KEY (proyecto_id) REFERENCES proyectos(id),
    CONSTRAINT fk_proyecto_servicio_servicio FOREIGN KEY (servicio_id) REFERENCES servicios(id)
);

CREATE TABLE productos (
    id UUID PRIMARY KEY,
    sku VARCHAR(100) NOT NULL,
    nombre VARCHAR(180) NOT NULL,
    slug VARCHAR(220) NOT NULL,
    resumen VARCHAR(500),
    descripcion TEXT NOT NULL,
    precio_base NUMERIC(12,2),
    precio_anterior NUMERIC(12,2),
    descuento_porcentaje NUMERIC(5,2),
    disponibilidad VARCHAR(30) NOT NULL,
    destacado BOOLEAN NOT NULL,
    estado VARCHAR(30) NOT NULL,
    titulo_seo VARCHAR(180),
    descripcion_seo VARCHAR(320),
    unidad_negocio_id UUID NOT NULL,
    fecha_creacion TIMESTAMP(6) NOT NULL,
    fecha_actualizacion TIMESTAMP(6) NOT NULL,
    CONSTRAINT uk_producto_sku UNIQUE (sku),
    CONSTRAINT uk_producto_slug UNIQUE (slug),
    CONSTRAINT fk_producto_unidad FOREIGN KEY (unidad_negocio_id) REFERENCES unidades_negocio(id)
);

CREATE TABLE categoria_producto (
    producto_id UUID NOT NULL,
    categoria_id UUID NOT NULL,
    PRIMARY KEY (producto_id, categoria_id),
    CONSTRAINT fk_categoria_producto_producto FOREIGN KEY (producto_id) REFERENCES productos(id),
    CONSTRAINT fk_categoria_producto_categoria FOREIGN KEY (categoria_id) REFERENCES categorias_producto(id)
);

CREATE TABLE variantes_producto (
    id UUID PRIMARY KEY,
    sku VARCHAR(100) NOT NULL,
    nombre VARCHAR(150) NOT NULL,
    descripcion VARCHAR(500),
    precio NUMERIC(12,2),
    disponible BOOLEAN NOT NULL,
    imagen_url VARCHAR(500),
    orden INTEGER NOT NULL,
    producto_id UUID NOT NULL,
    fecha_creacion TIMESTAMP(6) NOT NULL,
    fecha_actualizacion TIMESTAMP(6) NOT NULL,
    CONSTRAINT uk_variante_sku UNIQUE (sku),
    CONSTRAINT fk_variante_producto FOREIGN KEY (producto_id) REFERENCES productos(id)
);

CREATE TABLE imagenes_producto (
    id UUID PRIMARY KEY,
    url VARCHAR(500) NOT NULL,
    alt_text VARCHAR(200),
    es_principal BOOLEAN NOT NULL,
    orden INTEGER NOT NULL,
    producto_id UUID NOT NULL,
    fecha_creacion TIMESTAMP(6) NOT NULL,
    CONSTRAINT fk_imagen_producto FOREIGN KEY (producto_id) REFERENCES productos(id)
);

CREATE TABLE especificaciones_producto (
    id UUID PRIMARY KEY,
    clave VARCHAR(100) NOT NULL,
    valor VARCHAR(500) NOT NULL,
    grupo VARCHAR(100),
    orden INTEGER NOT NULL,
    producto_id UUID NOT NULL,
    CONSTRAINT fk_especificacion_producto FOREIGN KEY (producto_id) REFERENCES productos(id)
);

CREATE TABLE documentos_producto (
    id UUID PRIMARY KEY,
    titulo VARCHAR(180) NOT NULL,
    tipo VARCHAR(40) NOT NULL,
    url VARCHAR(500) NOT NULL,
    formato VARCHAR(20),
    tamano_bytes BIGINT,
    producto_id UUID NOT NULL,
    fecha_creacion TIMESTAMP(6) NOT NULL,
    CONSTRAINT fk_documento_producto FOREIGN KEY (producto_id) REFERENCES productos(id)
);

CREATE TABLE configuraciones_calculo (
    id UUID PRIMARY KEY,
    habilitada BOOLEAN NOT NULL,
    etiqueta_entrada VARCHAR(100),
    unidad_entrada VARCHAR(50),
    cobertura_por_unidad NUMERIC(12,4) NOT NULL,
    unidad_venta VARCHAR(50),
    texto_ayuda VARCHAR(255),
    producto_id UUID NOT NULL,
    fecha_actualizacion TIMESTAMP(6) NOT NULL,
    CONSTRAINT uk_configuracion_calculo_producto UNIQUE (producto_id),
    CONSTRAINT fk_configuracion_calculo_producto FOREIGN KEY (producto_id) REFERENCES productos(id)
);

CREATE TABLE cotizaciones (
    id UUID PRIMARY KEY,
    codigo VARCHAR(50) NOT NULL,
    nombre_cliente VARCHAR(150) NOT NULL,
    email_cliente VARCHAR(120) NOT NULL,
    telefono_cliente VARCHAR(30) NOT NULL,
    empresa_cliente VARCHAR(150),
    ciudad VARCHAR(100),
    mensaje TEXT,
    canal VARCHAR(30) NOT NULL,
    estado VARCHAR(30) NOT NULL,
    total_estimado NUMERIC(12,2),
    fecha_creacion TIMESTAMP(6) NOT NULL,
    fecha_actualizacion TIMESTAMP(6) NOT NULL,
    CONSTRAINT uk_cotizacion_codigo UNIQUE (codigo)
);

CREATE TABLE detalles_cotizacion (
    id UUID PRIMARY KEY,
    nombre_producto VARCHAR(180) NOT NULL,
    sku VARCHAR(100),
    cantidad INTEGER NOT NULL,
    precio_unitario NUMERIC(12,2),
    subtotal NUMERIC(12,2),
    notas VARCHAR(500),
    cotizacion_id UUID NOT NULL,
    producto_id UUID NOT NULL,
    variante_id UUID,
    CONSTRAINT fk_detalle_cotizacion FOREIGN KEY (cotizacion_id) REFERENCES cotizaciones(id),
    CONSTRAINT fk_detalle_producto FOREIGN KEY (producto_id) REFERENCES productos(id),
    CONSTRAINT fk_detalle_variante FOREIGN KEY (variante_id) REFERENCES variantes_producto(id)
);

CREATE TABLE administradores (
    id UUID PRIMARY KEY,
    nombre VARCHAR(120) NOT NULL,
    apellido VARCHAR(120),
    email VARCHAR(150) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    telefono VARCHAR(30),
    activo BOOLEAN NOT NULL,
    ultimo_acceso TIMESTAMP(6),
    fecha_creacion TIMESTAMP(6) NOT NULL,
    fecha_actualizacion TIMESTAMP(6) NOT NULL,
    CONSTRAINT uk_administrador_email UNIQUE (email)
);

CREATE TABLE seguimientos_cotizacion (
    id UUID PRIMARY KEY,
    estado_anterior VARCHAR(30),
    estado_nuevo VARCHAR(30) NOT NULL,
    comentario TEXT NOT NULL,
    fecha TIMESTAMP(6) NOT NULL,
    cotizacion_id UUID NOT NULL,
    administrador_id UUID,
    CONSTRAINT fk_seguimiento_cotizacion FOREIGN KEY (cotizacion_id) REFERENCES cotizaciones(id),
    CONSTRAINT fk_seguimiento_administrador FOREIGN KEY (administrador_id) REFERENCES administradores(id)
);

CREATE TABLE roles (
    id UUID PRIMARY KEY,
    nombre VARCHAR(60) NOT NULL,
    descripcion VARCHAR(255),
    activo BOOLEAN NOT NULL,
    fecha_creacion TIMESTAMP(6) NOT NULL,
    fecha_actualizacion TIMESTAMP(6) NOT NULL,
    CONSTRAINT uk_rol_nombre UNIQUE (nombre)
);

CREATE TABLE permisos (
    id UUID PRIMARY KEY,
    codigo VARCHAR(100) NOT NULL,
    nombre VARCHAR(80) NOT NULL,
    descripcion VARCHAR(255),
    modulo VARCHAR(60),
    fecha_creacion TIMESTAMP(6) NOT NULL,
    CONSTRAINT uk_permiso_codigo UNIQUE (codigo),
    CONSTRAINT uk_permiso_nombre UNIQUE (nombre)
);

CREATE TABLE administrador_rol (
    administrador_id UUID NOT NULL,
    rol_id UUID NOT NULL,
    PRIMARY KEY (administrador_id, rol_id),
    CONSTRAINT fk_administrador_rol_administrador FOREIGN KEY (administrador_id) REFERENCES administradores(id),
    CONSTRAINT fk_administrador_rol_rol FOREIGN KEY (rol_id) REFERENCES roles(id)
);

CREATE TABLE rol_permiso (
    rol_id UUID NOT NULL,
    permiso_id UUID NOT NULL,
    PRIMARY KEY (rol_id, permiso_id),
    CONSTRAINT fk_rol_permiso_rol FOREIGN KEY (rol_id) REFERENCES roles(id),
    CONSTRAINT fk_rol_permiso_permiso FOREIGN KEY (permiso_id) REFERENCES permisos(id)
);


-- ---- Contenido dinámico ----
-- Contenido dinámico: clave del sitio, secciones, escenas, acciones, SEO y estadísticas.

ALTER TABLE configuraciones_sitio ADD COLUMN clave VARCHAR(100);
ALTER TABLE configuraciones_sitio ALTER COLUMN clave SET NOT NULL;
ALTER TABLE configuraciones_sitio ADD CONSTRAINT uk_configuracion_sitio_clave UNIQUE (clave);

ALTER TABLE secciones_landing ADD COLUMN etiqueta VARCHAR(180);
ALTER TABLE secciones_landing ADD COLUMN imagen_alt VARCHAR(300);
ALTER TABLE secciones_landing ADD CONSTRAINT secciones_landing_tipo_check CHECK (
    tipo IN ('HERO', 'EMPRESA', 'SERVICIOS', 'UNIDAD_NEGOCIO', 'PROYECTOS', 'CONTACTO', 'CTA', 'PERSONALIZADA')
);
ALTER TABLE secciones_landing ADD CONSTRAINT uk_seccion_landing_sitio_tipo
    UNIQUE (configuracion_sitio_id, tipo);
ALTER TABLE secciones_landing ADD CONSTRAINT ck_seccion_landing_orden CHECK (orden >= 0);

ALTER TABLE unidades_negocio ADD COLUMN destacado BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE unidades_negocio ADD COLUMN imagen_alt VARCHAR(300);
ALTER TABLE unidades_negocio ADD CONSTRAINT ck_unidad_negocio_orden CHECK (orden >= 0);

ALTER TABLE servicios ADD COLUMN etiqueta VARCHAR(180);
ALTER TABLE servicios ADD COLUMN imagen_alt VARCHAR(300);
ALTER TABLE servicios ADD CONSTRAINT ck_servicio_orden CHECK (orden >= 0);

ALTER TABLE proyectos ADD COLUMN orden INTEGER NOT NULL DEFAULT 0;
ALTER TABLE proyectos ADD CONSTRAINT ck_proyecto_orden CHECK (orden >= 0);


CREATE TABLE hero_scenes (
    id UUID PRIMARY KEY,
    imagen_url VARCHAR(500) NOT NULL,
    alt VARCHAR(300),
    orden INTEGER NOT NULL DEFAULT 0,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    seccion_landing_id UUID NOT NULL,
    fecha_creacion TIMESTAMP(6) NOT NULL,
    fecha_actualizacion TIMESTAMP(6) NOT NULL,
    CONSTRAINT ck_hero_scene_orden CHECK (orden >= 0),
    CONSTRAINT fk_hero_scene_seccion FOREIGN KEY (seccion_landing_id) REFERENCES secciones_landing(id)
);
CREATE INDEX ix_hero_scene_seccion ON hero_scenes(seccion_landing_id);

CREATE TABLE acciones_landing (
    id UUID PRIMARY KEY,
    texto VARCHAR(80) NOT NULL,
    enlace VARCHAR(500) NOT NULL,
    orden INTEGER NOT NULL DEFAULT 0,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    seccion_landing_id UUID NOT NULL,
    fecha_creacion TIMESTAMP(6) NOT NULL,
    fecha_actualizacion TIMESTAMP(6) NOT NULL,
    CONSTRAINT ck_accion_landing_orden CHECK (orden >= 0),
    CONSTRAINT fk_accion_landing_seccion FOREIGN KEY (seccion_landing_id) REFERENCES secciones_landing(id)
);
CREATE INDEX ix_accion_landing_seccion ON acciones_landing(seccion_landing_id);

CREATE TABLE recursos_unidad_negocio (
    id UUID PRIMARY KEY,
    tipo VARCHAR(40) NOT NULL,
    url VARCHAR(500) NOT NULL,
    alt VARCHAR(300),
    etiqueta VARCHAR(120),
    orden INTEGER NOT NULL DEFAULT 0,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    unidad_negocio_id UUID NOT NULL,
    fecha_creacion TIMESTAMP(6) NOT NULL,
    fecha_actualizacion TIMESTAMP(6) NOT NULL,
    CONSTRAINT ck_recurso_unidad_tipo CHECK (tipo IN ('IMAGEN_FONDO', 'IMAGEN_EDITORIAL', 'CATALOGO')),
    CONSTRAINT ck_recurso_unidad_orden CHECK (orden >= 0),
    CONSTRAINT fk_recurso_unidad FOREIGN KEY (unidad_negocio_id) REFERENCES unidades_negocio(id)
);
CREATE INDEX ix_recurso_unidad ON recursos_unidad_negocio(unidad_negocio_id);

CREATE TABLE beneficios_servicio (
    id UUID PRIMARY KEY,
    texto VARCHAR(300) NOT NULL,
    orden INTEGER NOT NULL DEFAULT 0,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    servicio_id UUID NOT NULL,
    fecha_creacion TIMESTAMP(6) NOT NULL,
    fecha_actualizacion TIMESTAMP(6) NOT NULL,
    CONSTRAINT ck_beneficio_servicio_orden CHECK (orden >= 0),
    CONSTRAINT fk_beneficio_servicio FOREIGN KEY (servicio_id) REFERENCES servicios(id)
);
CREATE INDEX ix_beneficio_servicio ON beneficios_servicio(servicio_id);

CREATE TABLE estadisticas_empresa (
    id UUID PRIMARY KEY,
    valor NUMERIC(19,2) NOT NULL,
    prefijo VARCHAR(10),
    sufijo VARCHAR(10),
    etiqueta VARCHAR(120) NOT NULL,
    orden INTEGER NOT NULL DEFAULT 0,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    empresa_id UUID NOT NULL,
    fecha_creacion TIMESTAMP(6) NOT NULL,
    fecha_actualizacion TIMESTAMP(6) NOT NULL,
    CONSTRAINT ck_estadistica_valor CHECK (valor >= 0),
    CONSTRAINT ck_estadistica_orden CHECK (orden >= 0),
    CONSTRAINT fk_estadistica_empresa FOREIGN KEY (empresa_id) REFERENCES empresa(id)
);
CREATE INDEX ix_estadistica_empresa ON estadisticas_empresa(empresa_id);

CREATE TABLE contenidos_pagina (
    id UUID PRIMARY KEY,
    pagina VARCHAR(30) NOT NULL,
    eyebrow VARCHAR(180),
    titulo VARCHAR(220),
    introduccion TEXT,
    descripcion TEXT,
    imagen_url VARCHAR(500),
    imagen_alt VARCHAR(300),
    imagen_fondo_url VARCHAR(500),
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    configuracion_sitio_id UUID NOT NULL,
    fecha_creacion TIMESTAMP(6) NOT NULL,
    fecha_actualizacion TIMESTAMP(6) NOT NULL,
    CONSTRAINT ck_contenido_pagina_tipo CHECK (pagina IN ('NOSOTROS', 'SERVICIOS', 'PROYECTOS', 'CONTACTO')),
    CONSTRAINT uk_contenido_pagina_sitio_tipo UNIQUE (configuracion_sitio_id, pagina),
    CONSTRAINT fk_contenido_pagina_sitio FOREIGN KEY (configuracion_sitio_id) REFERENCES configuraciones_sitio(id)
);

CREATE TABLE contenido_pagina_tags (
    contenido_pagina_id UUID NOT NULL,
    orden INTEGER NOT NULL,
    tag VARCHAR(80) NOT NULL,
    PRIMARY KEY (contenido_pagina_id, orden),
    CONSTRAINT ck_contenido_tag_orden CHECK (orden >= 0),
    CONSTRAINT fk_contenido_tag_pagina FOREIGN KEY (contenido_pagina_id) REFERENCES contenidos_pagina(id)
);

CREATE TABLE seo_paginas (
    id UUID PRIMARY KEY,
    tipo_pagina VARCHAR(30) NOT NULL,
    title VARCHAR(180) NOT NULL,
    description VARCHAR(320) NOT NULL,
    og_image_url VARCHAR(500),
    robots VARCHAR(30) NOT NULL DEFAULT 'INDEX_FOLLOW',
    configuracion_sitio_id UUID NOT NULL,
    unidad_negocio_id UUID,
    fecha_creacion TIMESTAMP(6) NOT NULL,
    fecha_actualizacion TIMESTAMP(6) NOT NULL,
    CONSTRAINT ck_seo_tipo CHECK (tipo_pagina IN ('HOME', 'NOSOTROS', 'SERVICIOS', 'PROYECTOS', 'CONTACTO', 'UNIDAD_NEGOCIO')),
    CONSTRAINT ck_seo_robots CHECK (robots IN ('INDEX_FOLLOW', 'NOINDEX_FOLLOW', 'INDEX_NOFOLLOW', 'NOINDEX_NOFOLLOW')),
    CONSTRAINT ck_seo_unidad_coherente CHECK (
        (tipo_pagina = 'UNIDAD_NEGOCIO' AND unidad_negocio_id IS NOT NULL)
        OR (tipo_pagina <> 'UNIDAD_NEGOCIO' AND unidad_negocio_id IS NULL)
    ),
    CONSTRAINT fk_seo_sitio FOREIGN KEY (configuracion_sitio_id) REFERENCES configuraciones_sitio(id),
    CONSTRAINT fk_seo_unidad FOREIGN KEY (unidad_negocio_id) REFERENCES unidades_negocio(id)
);
CREATE INDEX ix_seo_sitio ON seo_paginas(configuracion_sitio_id);

CREATE TABLE solicitudes_contacto (
    id UUID PRIMARY KEY,
    nombre VARCHAR(150) NOT NULL,
    email VARCHAR(180) NOT NULL,
    telefono VARCHAR(30) NOT NULL,
    empresa VARCHAR(180),
    mensaje TEXT NOT NULL,
    estado VARCHAR(30) NOT NULL DEFAULT 'NUEVA',
    fecha_creacion TIMESTAMP(6) NOT NULL,
    fecha_actualizacion TIMESTAMP(6) NOT NULL,
    CONSTRAINT ck_solicitud_contacto_estado CHECK (estado IN ('NUEVA', 'EN_GESTION', 'RESPONDIDA', 'DESCARTADA', 'SPAM')),
    CONSTRAINT ck_solicitud_contacto_email CHECK (email LIKE '%_@_%._%')
);


-- ---- Imagen directa en proyectos ----
ALTER TABLE proyectos ADD COLUMN imagen_url VARCHAR(500);
ALTER TABLE proyectos ADD COLUMN imagen_alt VARCHAR(300);


-- ---- Cuentas de clientes ----
CREATE TABLE IF NOT EXISTS clientes (
    id UUID PRIMARY KEY,
    nombre VARCHAR(120) NOT NULL,
    apellido VARCHAR(120),
    email VARCHAR(150) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    telefono VARCHAR(30),
    activo BOOLEAN NOT NULL,
    consentimiento_datos BOOLEAN NOT NULL,
    fecha_consentimiento TIMESTAMP(6),
    ultimo_acceso TIMESTAMP(6),
    fecha_creacion TIMESTAMP(6) NOT NULL,
    fecha_actualizacion TIMESTAMP(6) NOT NULL,
    CONSTRAINT uk_cliente_email UNIQUE (email)
);

ALTER TABLE cotizaciones ADD COLUMN IF NOT EXISTS cliente_id UUID;

ALTER TABLE cotizaciones
    ADD CONSTRAINT fk_cotizacion_cliente FOREIGN KEY (cliente_id) REFERENCES clientes (id);

CREATE INDEX IF NOT EXISTS idx_cotizaciones_cliente_id ON cotizaciones (cliente_id);


-- ---- Carrito de cliente ----
CREATE TABLE IF NOT EXISTS carritos_cliente (
    cliente_id UUID PRIMARY KEY,
    contenido TEXT NOT NULL,
    fecha_actualizacion TIMESTAMP(6) NOT NULL,
    CONSTRAINT fk_carrito_cliente FOREIGN KEY (cliente_id) REFERENCES clientes (id) ON DELETE CASCADE
);


-- ---- Retiro en tienda ----
ALTER TABLE productos ADD COLUMN retiro_en_tienda BOOLEAN DEFAULT FALSE;


-- ---- Reseñas de producto ----
CREATE TABLE resenas_producto (
    id UUID PRIMARY KEY,
    producto_id UUID NOT NULL,
    cliente_id UUID NOT NULL,
    nombre_cliente VARCHAR(160) NOT NULL,
    calificacion INTEGER NOT NULL,
    titulo VARCHAR(160),
    comentario TEXT NOT NULL,
    fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    compra_verificada BOOLEAN NOT NULL DEFAULT FALSE,
    cantidad_util INTEGER NOT NULL DEFAULT 0,
    CONSTRAINT fk_resena_producto FOREIGN KEY (producto_id) REFERENCES productos(id) ON DELETE CASCADE,
    CONSTRAINT chk_resena_calificacion CHECK (calificacion BETWEEN 1 AND 5),
    CONSTRAINT chk_resena_cantidad_util CHECK (cantidad_util >= 0)
);

CREATE INDEX idx_resenas_producto_fecha
    ON resenas_producto (producto_id, fecha_creacion DESC);


-- ---- Anuncios ----
CREATE TABLE anuncios (
    id UUID PRIMARY KEY,
    titulo VARCHAR(160) NOT NULL,
    descripcion_resumida VARCHAR(320) NOT NULL,
    contenido_detallado TEXT NOT NULL,
    condiciones TEXT,
    imagen_url VARCHAR(500) NOT NULL,
    etiqueta VARCHAR(60) NOT NULL,
    destino VARCHAR(20) NOT NULL,
    tipo_accion VARCHAR(30) NOT NULL,
    destino_accion VARCHAR(500) NOT NULL,
    texto_boton VARCHAR(80) NOT NULL,
    fecha_inicio TIMESTAMP(6),
    fecha_fin TIMESTAMP(6),
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    orden INTEGER NOT NULL DEFAULT 0,
    fecha_creacion TIMESTAMP(6) NOT NULL,
    fecha_actualizacion TIMESTAMP(6) NOT NULL,
    CONSTRAINT chk_anuncios_destino CHECK (destino IN ('ISANORTE', 'ISADECOR', 'AMBOS')),
    CONSTRAINT chk_anuncios_tipo_accion CHECK (tipo_accion IN ('RUTA_INTERNA', 'URL_EXTERNA', 'SECCION', 'PRODUCTO', 'CATEGORIA', 'PROYECTO', 'SERVICIO')),
    CONSTRAINT chk_anuncios_orden CHECK (orden >= 0),
    CONSTRAINT chk_anuncios_vigencia CHECK (fecha_fin IS NULL OR fecha_inicio IS NULL OR fecha_fin >= fecha_inicio)
);

CREATE INDEX idx_anuncios_publicos ON anuncios (destino, activo, orden, fecha_inicio, fecha_fin);


-- ---- Home de ISADECOR administrable ----
ALTER TABLE anuncios ADD COLUMN imagen_hover_url VARCHAR(500);
ALTER TABLE anuncios ADD COLUMN ubicacion VARCHAR(30) NOT NULL DEFAULT 'GENERAL';

ALTER TABLE anuncios
    ADD CONSTRAINT chk_anuncios_ubicacion CHECK (ubicacion IN (
        'GENERAL', 'CINTA_HOME', 'PROMOCION_PRINCIPAL', 'PROMOCION_SECUNDARIA',
        'ANUNCIOS_HOME', 'CATEGORIA_SUPERIOR'
    ));

CREATE INDEX idx_anuncios_home_publicos
    ON anuncios (destino, ubicacion, activo, orden, fecha_inicio, fecha_fin);

ALTER TABLE productos ADD COLUMN orden_destacado INTEGER NOT NULL DEFAULT 0;
ALTER TABLE productos
    ADD CONSTRAINT chk_productos_orden_destacado CHECK (orden_destacado >= 0);

CREATE INDEX idx_productos_destacados
    ON productos (destacado, orden_destacado);


-- ---- Catálogos ----
CREATE TABLE catalogos (
    id UUID PRIMARY KEY,
    titulo VARCHAR(200) NOT NULL,
    imagen_portada_url VARCHAR(500) NOT NULL,
    archivo_url VARCHAR(500) NOT NULL,
    orden INTEGER NOT NULL DEFAULT 0,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    unidad_negocio_id UUID NOT NULL,
    fecha_creacion TIMESTAMP(6) NOT NULL,
    fecha_actualizacion TIMESTAMP(6) NOT NULL,
    CONSTRAINT ck_catalogos_orden CHECK (orden >= 0),
    CONSTRAINT fk_catalogos_unidad FOREIGN KEY (unidad_negocio_id) REFERENCES unidades_negocio(id)
);

CREATE INDEX ix_catalogos_unidad ON catalogos(unidad_negocio_id);


-- ---- Pedidos y direcciones ----
CREATE TABLE pedidos (
    id UUID PRIMARY KEY,
    codigo VARCHAR(50) NOT NULL,
    cliente_id UUID NOT NULL,
    direccion_alias VARCHAR(120),
    direccion_calle VARCHAR(255) NOT NULL,
    direccion_distrito VARCHAR(120) NOT NULL,
    direccion_ciudad VARCHAR(120) NOT NULL,
    direccion_referencia VARCHAR(255),
    direccion_telefono_contacto VARCHAR(30) NOT NULL,
    ruc VARCHAR(20),
    razon_social VARCHAR(200),
    estado VARCHAR(30) NOT NULL,
    total_pedido NUMERIC(12,2),
    stock_descontado BOOLEAN NOT NULL DEFAULT FALSE,
    fecha_creacion TIMESTAMP(6) NOT NULL,
    fecha_actualizacion TIMESTAMP(6) NOT NULL,
    CONSTRAINT uk_pedido_codigo UNIQUE (codigo),
    CONSTRAINT fk_pedido_cliente FOREIGN KEY (cliente_id) REFERENCES clientes(id)
);
CREATE INDEX ix_pedidos_cliente ON pedidos(cliente_id);

CREATE TABLE detalles_pedido (
    id UUID PRIMARY KEY,
    nombre_producto VARCHAR(180) NOT NULL,
    sku VARCHAR(100),
    cantidad INTEGER NOT NULL,
    precio_unitario NUMERIC(12,2),
    subtotal NUMERIC(12,2),
    notas VARCHAR(500),
    pedido_id UUID NOT NULL,
    producto_id UUID NOT NULL,
    variante_id UUID,
    CONSTRAINT fk_detalle_pedido_pedido FOREIGN KEY (pedido_id) REFERENCES pedidos(id),
    CONSTRAINT fk_detalle_pedido_producto FOREIGN KEY (producto_id) REFERENCES productos(id),
    CONSTRAINT fk_detalle_pedido_variante FOREIGN KEY (variante_id) REFERENCES variantes_producto(id)
);

CREATE TABLE seguimientos_pedido (
    id UUID PRIMARY KEY,
    estado_anterior VARCHAR(30),
    estado_nuevo VARCHAR(30) NOT NULL,
    comentario TEXT NOT NULL,
    fecha TIMESTAMP(6) NOT NULL,
    pedido_id UUID NOT NULL,
    administrador_id UUID,
    CONSTRAINT fk_seguimiento_pedido_pedido FOREIGN KEY (pedido_id) REFERENCES pedidos(id),
    CONSTRAINT fk_seguimiento_pedido_administrador FOREIGN KEY (administrador_id) REFERENCES administradores(id)
);

CREATE TABLE direcciones_cliente (
    id UUID PRIMARY KEY,
    cliente_id UUID NOT NULL,
    alias VARCHAR(120),
    calle VARCHAR(255) NOT NULL,
    distrito VARCHAR(120) NOT NULL,
    ciudad VARCHAR(120) NOT NULL,
    referencia VARCHAR(255),
    telefono_contacto VARCHAR(30) NOT NULL,
    principal BOOLEAN NOT NULL DEFAULT FALSE,
    fecha_creacion TIMESTAMP(6) NOT NULL,
    fecha_actualizacion TIMESTAMP(6) NOT NULL,
    CONSTRAINT fk_direccion_cliente FOREIGN KEY (cliente_id) REFERENCES clientes(id) ON DELETE CASCADE
);
CREATE INDEX ix_direcciones_cliente_cliente ON direcciones_cliente(cliente_id);

ALTER TABLE productos ADD COLUMN stock_actual INTEGER;
ALTER TABLE productos ADD COLUMN version INTEGER NOT NULL DEFAULT 0;
