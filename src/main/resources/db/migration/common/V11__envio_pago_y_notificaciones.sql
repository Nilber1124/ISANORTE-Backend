-- Envío y pago del pedido online, y bandeja de notificaciones del administrador.
ALTER TABLE pedidos ADD COLUMN metodo_entrega VARCHAR(20) NOT NULL DEFAULT 'DELIVERY';
ALTER TABLE pedidos ADD COLUMN costo_envio NUMERIC(12,2) NOT NULL DEFAULT 0;
ALTER TABLE pedidos ADD COLUMN metodo_pago VARCHAR(30);
ALTER TABLE pedidos ADD COLUMN referencia_pago VARCHAR(80);
ALTER TABLE pedidos ADD COLUMN fecha_pago TIMESTAMP(6);

CREATE TABLE notificaciones (
    id UUID PRIMARY KEY,
    tipo VARCHAR(30) NOT NULL,
    titulo VARCHAR(160) NOT NULL,
    mensaje VARCHAR(500) NOT NULL,
    enlace VARCHAR(200),
    leida BOOLEAN NOT NULL DEFAULT FALSE,
    fecha_creacion TIMESTAMP(6) NOT NULL
);
CREATE INDEX ix_notificaciones_leida_fecha ON notificaciones(leida, fecha_creacion);
