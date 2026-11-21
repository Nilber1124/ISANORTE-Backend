-- Subcategorías: una categoría puede colgar de otra (máximo dos niveles, validado en el servicio).
ALTER TABLE categorias_producto ADD COLUMN categoria_padre_id UUID;
ALTER TABLE categorias_producto ADD CONSTRAINT fk_categoria_padre
    FOREIGN KEY (categoria_padre_id) REFERENCES categorias_producto(id) ON DELETE SET NULL;
CREATE INDEX idx_categorias_producto_padre ON categorias_producto (categoria_padre_id);
