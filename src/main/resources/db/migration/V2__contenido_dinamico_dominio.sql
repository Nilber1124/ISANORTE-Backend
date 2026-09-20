-- Evolución aditiva del dominio de contenido dinámico. No elimina ni reemplaza datos existentes.

ALTER TABLE configuraciones_sitio ADD COLUMN clave VARCHAR(100);
UPDATE configuraciones_sitio SET clave = CONCAT('site-', CAST(id AS VARCHAR)) WHERE clave IS NULL;
ALTER TABLE configuraciones_sitio ALTER COLUMN clave SET NOT NULL;
ALTER TABLE configuraciones_sitio ADD CONSTRAINT uk_configuracion_sitio_clave UNIQUE (clave);

ALTER TABLE secciones_landing ADD COLUMN etiqueta VARCHAR(180);
ALTER TABLE secciones_landing ADD COLUMN imagen_alt VARCHAR(300);
ALTER TABLE secciones_landing DROP CONSTRAINT IF EXISTS secciones_landing_tipo_check;
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

ALTER TABLE imagenes_proyecto ADD COLUMN alt VARCHAR(300);
ALTER TABLE imagenes_proyecto ADD CONSTRAINT ck_imagen_proyecto_orden CHECK (orden >= 0);

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
