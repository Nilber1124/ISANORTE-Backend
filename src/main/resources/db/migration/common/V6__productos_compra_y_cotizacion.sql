-- Todo producto puede comprarse y cotizarse: la modalidad por defecto pasa a AMBAS.
-- COMPRAR y COTIZAR siguen existiendo para restringir un producto a una sola vía.
ALTER TABLE productos DROP CONSTRAINT chk_productos_modalidad;
ALTER TABLE productos ADD CONSTRAINT chk_productos_modalidad CHECK (modalidad IN ('COMPRAR', 'COTIZAR', 'AMBAS'));
ALTER TABLE productos ALTER COLUMN modalidad SET DEFAULT 'AMBAS';
UPDATE productos SET modalidad = 'AMBAS';
