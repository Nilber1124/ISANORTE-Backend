-- Textos de cabecera de los bloques de la Home (frase pequeña, título y subtítulo), editables desde el admin.
-- Sin fila para una clave, la Home muestra su texto por defecto.
CREATE TABLE textos_bloque (
    id UUID PRIMARY KEY,
    clave VARCHAR(60) NOT NULL,
    etiqueta VARCHAR(150),
    titulo VARCHAR(300),
    subtitulo VARCHAR(600),
    fecha_actualizacion TIMESTAMP(6) NOT NULL,
    CONSTRAINT uk_textos_bloque_clave UNIQUE (clave)
);
