-- Consolidación ligera de tablas de detalle (auditoría de BD del 2026-11-23). La API no cambia.
-- 1) isadecor_seccion_fijados + isadecor_seccion_excluidos -> isadecor_seccion_seleccion (columna tipo).
-- 2) contenido_pagina_tags -> columna contenidos_pagina.tags (una etiqueta por línea).
-- 3) isadecor_servicio_puntos -> columna isadecor_servicios_empresa.puntos (arreglo JSON).
-- Copia los datos existentes antes de borrar cada tabla.

-- ---- 1) Selección manual de productos por sección ----
CREATE TABLE isadecor_seccion_seleccion (
    id UUID PRIMARY KEY,
    seccion_id UUID NOT NULL,
    producto_id UUID NOT NULL,
    tipo VARCHAR(10) NOT NULL,
    orden INTEGER NOT NULL DEFAULT 0,
    CONSTRAINT uk_isadecor_seccion_seleccion UNIQUE (seccion_id, producto_id, tipo),
    CONSTRAINT chk_isadecor_seccion_seleccion_tipo CHECK (tipo IN ('FIJADO', 'EXCLUIDO')),
    CONSTRAINT chk_isadecor_seccion_seleccion_orden CHECK (orden >= 0),
    CONSTRAINT fk_isadecor_seccion_seleccion_seccion FOREIGN KEY (seccion_id) REFERENCES isadecor_secciones_productos(id) ON DELETE CASCADE,
    CONSTRAINT fk_isadecor_seccion_seleccion_producto FOREIGN KEY (producto_id) REFERENCES productos(id)
);

INSERT INTO isadecor_seccion_seleccion (id, seccion_id, producto_id, tipo, orden)
SELECT id, seccion_id, producto_id, 'FIJADO', orden FROM isadecor_seccion_fijados;

INSERT INTO isadecor_seccion_seleccion (id, seccion_id, producto_id, tipo, orden)
SELECT id, seccion_id, producto_id, 'EXCLUIDO', 0 FROM isadecor_seccion_excluidos;

DROP TABLE isadecor_seccion_fijados;
DROP TABLE isadecor_seccion_excluidos;

-- ---- 2) Etiquetas de las páginas de contenido ----
ALTER TABLE contenidos_pagina ADD COLUMN tags VARCHAR(2000);

UPDATE contenidos_pagina c SET tags = (
    SELECT STRING_AGG(t.tag, CHR(10) ORDER BY t.orden)
    FROM contenido_pagina_tags t
    WHERE t.contenido_pagina_id = c.id
);

DROP TABLE contenido_pagina_tags;

-- ---- 3) Puntos del bloque de servicios de ISADECOR ----
ALTER TABLE isadecor_servicios_empresa ADD COLUMN puntos TEXT;

UPDATE isadecor_servicios_empresa s SET puntos = (
    SELECT '[' || STRING_AGG(
        '{"id":"' || CAST(p.id AS VARCHAR(36))
        || '","texto":"' || REPLACE(REPLACE(p.texto, '\', '\\'), '"', '\"')
        || '","orden":' || CAST(p.orden AS VARCHAR(11))
        || ',"fechaCreacion":"' || REPLACE(CAST(p.fecha_creacion AS VARCHAR(40)), ' ', 'T')
        || '","fechaActualizacion":"' || REPLACE(CAST(p.fecha_actualizacion AS VARCHAR(40)), ' ', 'T')
        || '"}', ',' ORDER BY p.orden, p.id) || ']'
    FROM isadecor_servicio_puntos p
    WHERE p.servicio_id = s.id
);

DROP TABLE isadecor_servicio_puntos;
