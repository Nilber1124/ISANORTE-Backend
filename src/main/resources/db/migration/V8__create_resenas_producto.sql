CREATE TABLE resenas_producto (
    id UUID PRIMARY KEY,
    producto_id UUID NOT NULL,
    cliente_id UUID NOT NULL,
    nombre_cliente VARCHAR(160) NOT NULL,
    calificacion INTEGER NOT NULL,
    titulo VARCHAR(160),
    comentario TEXT NOT NULL,
    fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    compra_verificada BOOLEAN NOT NULL DEFAULT FALSE,
    cantidad_util INTEGER NOT NULL DEFAULT 0,
    CONSTRAINT fk_resena_producto FOREIGN KEY (producto_id) REFERENCES productos(id) ON DELETE CASCADE,
    CONSTRAINT chk_resena_calificacion CHECK (calificacion BETWEEN 1 AND 5),
    CONSTRAINT chk_resena_cantidad_util CHECK (cantidad_util >= 0)
);

CREATE INDEX idx_resenas_producto_fecha
    ON resenas_producto (producto_id, fecha_creacion DESC);
