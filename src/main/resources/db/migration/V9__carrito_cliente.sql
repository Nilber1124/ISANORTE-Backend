CREATE TABLE IF NOT EXISTS carritos_cliente (
    cliente_id UUID PRIMARY KEY,
    contenido TEXT NOT NULL,
    fecha_actualizacion TIMESTAMP(6) NOT NULL,
    CONSTRAINT fk_carrito_cliente FOREIGN KEY (cliente_id) REFERENCES clientes (id) ON DELETE CASCADE
);
