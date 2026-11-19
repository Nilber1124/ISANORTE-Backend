-- Publicaciones (videos) de redes sociales que se muestran en la tienda: hoy se cargan por enlace
-- (origen MANUAL) y quedan preparadas para sincronizarse desde la API de cada red (origen API).
CREATE TABLE isadecor_publicaciones (
    id UUID PRIMARY KEY,
    unidad_negocio_id UUID NOT NULL,
    red VARCHAR(20) NOT NULL,
    url VARCHAR(500) NOT NULL,
    titulo VARCHAR(200),
    miniatura_url VARCHAR(500),
    origen VARCHAR(10) NOT NULL DEFAULT 'MANUAL',
    orden INTEGER NOT NULL DEFAULT 0,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    fecha_creacion TIMESTAMP(6) NOT NULL,
    fecha_actualizacion TIMESTAMP(6) NOT NULL,
    CONSTRAINT chk_isadecor_publicaciones_red CHECK (red IN ('INSTAGRAM', 'TIKTOK')),
    CONSTRAINT chk_isadecor_publicaciones_origen CHECK (origen IN ('MANUAL', 'API')),
    CONSTRAINT chk_isadecor_publicaciones_orden CHECK (orden >= 0),
    CONSTRAINT uk_isadecor_publicaciones_url UNIQUE (unidad_negocio_id, url),
    CONSTRAINT fk_isadecor_publicaciones_unidad FOREIGN KEY (unidad_negocio_id) REFERENCES unidades_negocio(id)
);
CREATE INDEX idx_isadecor_publicaciones_unidad ON isadecor_publicaciones (unidad_negocio_id, activo, orden);
