-- Datos mínimos para que la API pública responda en una base nueva: una empresa, su configuración
-- de sitio y la unidad de negocio ISADECOR. Son valores neutros y se editan desde el panel
-- administrativo (o con un script de datos). No contienen contenido comercial.
-- Las pruebas con H2 se detienen en V1 y no ejecutan esta migración.

INSERT INTO empresa (id, razon_social, nombre_comercial, ruc, fecha_creacion, fecha_actualizacion)
VALUES ('00000000-0000-4000-8000-000000000001', 'ISANORTE', 'ISANORTE', '00000000000',
        CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO configuraciones_sitio (id, clave, titulo_sitio, empresa_id, fecha_actualizacion)
VALUES ('00000000-0000-4000-8000-000000000002', 'isanorte', 'ISANORTE',
        '00000000-0000-4000-8000-000000000001', CURRENT_TIMESTAMP);

INSERT INTO unidades_negocio (id, nombre, slug, activo, destacado, orden, empresa_id,
                              fecha_creacion, fecha_actualizacion)
VALUES ('00000000-0000-4000-8000-000000000003', 'ISADECOR', 'isadecor', TRUE, TRUE, 0,
        '00000000-0000-4000-8000-000000000001', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
