ALTER TABLE anuncios
    ADD COLUMN imagen_hover_url VARCHAR(500),
    ADD COLUMN ubicacion VARCHAR(30) NOT NULL DEFAULT 'GENERAL';

ALTER TABLE anuncios
    ADD CONSTRAINT chk_anuncios_ubicacion CHECK (ubicacion IN (
        'GENERAL', 'CINTA_HOME', 'PROMOCION_PRINCIPAL', 'PROMOCION_SECUNDARIA',
        'ANUNCIOS_HOME', 'CATEGORIA_SUPERIOR'
    ));

CREATE INDEX idx_anuncios_home_publicos
    ON anuncios (destino, ubicacion, activo, orden, fecha_inicio, fecha_fin);

ALTER TABLE productos
    ADD COLUMN orden_destacado INTEGER NOT NULL DEFAULT 0,
    ADD CONSTRAINT chk_productos_orden_destacado CHECK (orden_destacado >= 0);

CREATE INDEX idx_productos_destacados
    ON productos (destacado, orden_destacado) WHERE destacado = TRUE;
