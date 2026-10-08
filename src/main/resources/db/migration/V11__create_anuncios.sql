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
    CONSTRAINT chk_anuncios_tipo_accion CHECK (tipo_accion IN (
        'RUTA_INTERNA', 'URL_EXTERNA', 'SECCION', 'PRODUCTO', 'CATEGORIA', 'PROYECTO', 'SERVICIO'
    )),
    CONSTRAINT chk_anuncios_orden CHECK (orden >= 0),
    CONSTRAINT chk_anuncios_vigencia CHECK (fecha_fin IS NULL OR fecha_inicio IS NULL OR fecha_fin >= fecha_inicio),
    CONSTRAINT chk_anuncios_textos CHECK (
        btrim(titulo) <> '' AND btrim(descripcion_resumida) <> '' AND
        btrim(contenido_detallado) <> '' AND btrim(imagen_url) <> '' AND
        btrim(etiqueta) <> '' AND btrim(destino_accion) <> '' AND btrim(texto_boton) <> ''
    )
);

CREATE INDEX idx_anuncios_publicos
    ON anuncios (destino, activo, orden, fecha_inicio, fecha_fin);

