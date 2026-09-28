ALTER TABLE productos ADD COLUMN IF NOT EXISTS retiro_en_tienda BOOLEAN DEFAULT FALSE;
UPDATE productos SET retiro_en_tienda = FALSE WHERE retiro_en_tienda IS NULL;
