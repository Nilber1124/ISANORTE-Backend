CREATE TABLE IF NOT EXISTS clientes (
    id UUID PRIMARY KEY,
    nombre VARCHAR(120) NOT NULL,
    apellido VARCHAR(120),
    email VARCHAR(150) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    telefono VARCHAR(30),
    activo BOOLEAN NOT NULL,
    consentimiento_datos BOOLEAN NOT NULL,
    fecha_consentimiento TIMESTAMP(6),
    ultimo_acceso TIMESTAMP(6),
    fecha_creacion TIMESTAMP(6) NOT NULL,
    fecha_actualizacion TIMESTAMP(6) NOT NULL,
    CONSTRAINT uk_cliente_email UNIQUE (email)
);

ALTER TABLE cotizaciones ADD COLUMN IF NOT EXISTS cliente_id UUID;

ALTER TABLE cotizaciones
    ADD CONSTRAINT fk_cotizacion_cliente FOREIGN KEY (cliente_id) REFERENCES clientes (id);

CREATE INDEX IF NOT EXISTS idx_cotizaciones_cliente_id ON cotizaciones (cliente_id);
