-- Una imagen puede pertenecer a un acabado (variante) del producto; sin acabado es una imagen general.
ALTER TABLE imagenes_producto ADD COLUMN variante_id UUID;
ALTER TABLE imagenes_producto ADD CONSTRAINT fk_imagen_producto_variante
    FOREIGN KEY (variante_id) REFERENCES variantes_producto(id) ON DELETE SET NULL;
CREATE INDEX idx_imagenes_producto_variante ON imagenes_producto (variante_id);
