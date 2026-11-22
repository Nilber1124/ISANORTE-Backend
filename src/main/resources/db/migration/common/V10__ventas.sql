-- Ventas registradas a mano en el admin (por WhatsApp, en tienda u otro canal). No dependen de una cuenta de
-- cliente. Al registrarlas, el admin descuenta el stock de los productos del catálogo (con su movimiento); si se
-- anulan, el stock se devuelve.
CREATE TABLE ventas (
    id UUID PRIMARY KEY,
    codigo VARCHAR(40) NOT NULL,
    canal VARCHAR(20) NOT NULL,
    fecha_venta DATE NOT NULL,
    cliente_nombre VARCHAR(150),
    cliente_telefono VARCHAR(30),
    tipo_documento VARCHAR(10),
    documento VARCHAR(20),
    razon_social VARCHAR(200),
    metodo_pago VARCHAR(20) NOT NULL,
    pagado BOOLEAN NOT NULL,
    total NUMERIC(12,2) NOT NULL,
    notas VARCHAR(500),
    anulada BOOLEAN NOT NULL DEFAULT FALSE,
    stock_descontado BOOLEAN NOT NULL DEFAULT FALSE,
    fecha_creacion TIMESTAMP(6) NOT NULL,
    fecha_actualizacion TIMESTAMP(6) NOT NULL,
    CONSTRAINT uk_ventas_codigo UNIQUE (codigo),
    CONSTRAINT ck_ventas_canal CHECK (canal IN ('WHATSAPP', 'TIENDA', 'OTRO')),
    CONSTRAINT ck_ventas_metodo_pago CHECK (metodo_pago IN ('EFECTIVO', 'TRANSFERENCIA', 'YAPE', 'PLIN', 'TARJETA', 'OTRO')),
    CONSTRAINT ck_ventas_tipo_documento CHECK (tipo_documento IS NULL OR tipo_documento IN ('DNI', 'RUC')),
    CONSTRAINT ck_ventas_total CHECK (total >= 0)
);
CREATE INDEX ix_ventas_fecha ON ventas (fecha_venta);

CREATE TABLE detalles_venta (
    id UUID PRIMARY KEY,
    venta_id UUID NOT NULL,
    producto_id UUID,
    variante_id UUID,
    nombre VARCHAR(180) NOT NULL,
    sku VARCHAR(100),
    cantidad INTEGER NOT NULL,
    precio_unitario NUMERIC(12,2) NOT NULL,
    subtotal NUMERIC(12,2) NOT NULL,
    CONSTRAINT fk_detalle_venta_venta FOREIGN KEY (venta_id) REFERENCES ventas(id),
    CONSTRAINT fk_detalle_venta_producto FOREIGN KEY (producto_id) REFERENCES productos(id),
    CONSTRAINT fk_detalle_venta_variante FOREIGN KEY (variante_id) REFERENCES variantes_producto(id),
    CONSTRAINT ck_detalle_venta_cantidad CHECK (cantidad > 0),
    CONSTRAINT ck_detalle_venta_precio CHECK (precio_unitario >= 0)
);
CREATE INDEX ix_detalles_venta_venta ON detalles_venta (venta_id);
