CREATE TABLE pedidos (
    id UUID PRIMARY KEY,
    codigo VARCHAR(50) NOT NULL,
    cliente_id UUID NOT NULL,
    direccion_alias VARCHAR(120),
    direccion_calle VARCHAR(255) NOT NULL,
    direccion_distrito VARCHAR(120) NOT NULL,
    direccion_ciudad VARCHAR(120) NOT NULL,
    direccion_referencia VARCHAR(255),
    direccion_telefono_contacto VARCHAR(30) NOT NULL,
    ruc VARCHAR(20),
    razon_social VARCHAR(200),
    estado VARCHAR(30) NOT NULL,
    total_pedido NUMERIC(12,2),
    stock_descontado BOOLEAN NOT NULL DEFAULT FALSE,
    fecha_creacion TIMESTAMP(6) NOT NULL,
    fecha_actualizacion TIMESTAMP(6) NOT NULL,
    CONSTRAINT uk_pedido_codigo UNIQUE (codigo),
    CONSTRAINT fk_pedido_cliente FOREIGN KEY (cliente_id) REFERENCES clientes(id)
);
CREATE INDEX ix_pedidos_cliente ON pedidos(cliente_id);

CREATE TABLE detalles_pedido (
    id UUID PRIMARY KEY,
    nombre_producto VARCHAR(180) NOT NULL,
    sku VARCHAR(100),
    cantidad INTEGER NOT NULL,
    precio_unitario NUMERIC(12,2),
    subtotal NUMERIC(12,2),
    notas VARCHAR(500),
    pedido_id UUID NOT NULL,
    producto_id UUID NOT NULL,
    variante_id UUID,
    CONSTRAINT fk_detalle_pedido_pedido FOREIGN KEY (pedido_id) REFERENCES pedidos(id),
    CONSTRAINT fk_detalle_pedido_producto FOREIGN KEY (producto_id) REFERENCES productos(id),
    CONSTRAINT fk_detalle_pedido_variante FOREIGN KEY (variante_id) REFERENCES variantes_producto(id)
);

CREATE TABLE seguimientos_pedido (
    id UUID PRIMARY KEY,
    estado_anterior VARCHAR(30),
    estado_nuevo VARCHAR(30) NOT NULL,
    comentario TEXT NOT NULL,
    fecha TIMESTAMP(6) NOT NULL,
    pedido_id UUID NOT NULL,
    administrador_id UUID,
    CONSTRAINT fk_seguimiento_pedido_pedido FOREIGN KEY (pedido_id) REFERENCES pedidos(id),
    CONSTRAINT fk_seguimiento_pedido_administrador FOREIGN KEY (administrador_id) REFERENCES administradores(id)
);

CREATE TABLE direcciones_cliente (
    id UUID PRIMARY KEY,
    cliente_id UUID NOT NULL,
    alias VARCHAR(120),
    calle VARCHAR(255) NOT NULL,
    distrito VARCHAR(120) NOT NULL,
    ciudad VARCHAR(120) NOT NULL,
    referencia VARCHAR(255),
    telefono_contacto VARCHAR(30) NOT NULL,
    principal BOOLEAN NOT NULL DEFAULT FALSE,
    fecha_creacion TIMESTAMP(6) NOT NULL,
    fecha_actualizacion TIMESTAMP(6) NOT NULL,
    CONSTRAINT fk_direccion_cliente FOREIGN KEY (cliente_id) REFERENCES clientes(id) ON DELETE CASCADE
);
CREATE INDEX ix_direcciones_cliente_cliente ON direcciones_cliente(cliente_id);

ALTER TABLE productos ADD COLUMN stock_actual INTEGER;
ALTER TABLE productos ADD COLUMN version INTEGER NOT NULL DEFAULT 0;
