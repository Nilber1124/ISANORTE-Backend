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
