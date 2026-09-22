-- 1. Agregar columnas directas de imagen a la tabla proyectos
ALTER TABLE proyectos ADD COLUMN IF NOT EXISTS imagen_url VARCHAR(500);
ALTER TABLE proyectos ADD COLUMN IF NOT EXISTS imagen_alt VARCHAR(300);

-- 2. Migrar la imagen principal existente (si hay datos en imagenes_proyecto)
DO $$
BEGIN
    IF EXISTS (SELECT FROM information_schema.tables WHERE table_name = 'imagenes_proyecto') THEN
        UPDATE proyectos p
        SET imagen_url = sub.url,
            imagen_alt = sub.alt
        FROM (
            SELECT DISTINCT ON (proyecto_id) proyecto_id, url, alt
            FROM imagenes_proyecto
            ORDER BY proyecto_id, es_principal DESC, orden ASC, id ASC
        ) sub
        WHERE p.id = sub.proyecto_id;
    END IF;
END $$;

-- 3. Eliminar tabla imagenes_proyecto
DROP TABLE IF EXISTS imagenes_proyecto CASCADE;
