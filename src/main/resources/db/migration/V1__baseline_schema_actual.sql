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
    icono VARCHAR(100),
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

CREATE TABLE imagenes_proyecto (
    id UUID PRIMARY KEY,
    url VARCHAR(500) NOT NULL,
    titulo VARCHAR(150),
    descripcion VARCHAR(300),
    tipo VARCHAR(30) NOT NULL,
    es_principal BOOLEAN NOT NULL,
    orden INTEGER NOT NULL,
    proyecto_id UUID NOT NULL,
    fecha_creacion TIMESTAMP(6) NOT NULL,
    CONSTRAINT fk_imagen_proyecto FOREIGN KEY (proyecto_id) REFERENCES proyectos(id)
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
