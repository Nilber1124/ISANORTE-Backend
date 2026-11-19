-- Home de ISADECOR administrable y stock mínimo viable (plan: docs/isadecor/PLAN-BACKEND.md del frontend).
-- Migración aditiva y portable (sin sintaxis exclusiva de PostgreSQL): no modifica datos existentes.

-- ---- Productos: modalidad de venta, unidad, entrega, "fácil de instalar" y umbral de stock ----
ALTER TABLE productos ADD COLUMN modalidad VARCHAR(10) NOT NULL DEFAULT 'COTIZAR';
ALTER TABLE productos ADD COLUMN unidad_venta VARCHAR(10) NOT NULL DEFAULT 'UNIDAD';
ALTER TABLE productos ADD COLUMN plazo_entrega VARCHAR(80);
ALTER TABLE productos ADD COLUMN facil_instalacion BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE productos ADD COLUMN stock_minimo INTEGER;

ALTER TABLE productos ADD CONSTRAINT chk_productos_modalidad CHECK (modalidad IN ('COMPRAR', 'COTIZAR'));
ALTER TABLE productos ADD CONSTRAINT chk_productos_unidad_venta CHECK (unidad_venta IN ('M2', 'UNIDAD', 'CAJA', 'ML'));
ALTER TABLE productos ADD CONSTRAINT chk_productos_stock_minimo CHECK (stock_minimo IS NULL OR stock_minimo >= 0);
ALTER TABLE productos ADD CONSTRAINT chk_productos_stock_actual CHECK (stock_actual IS NULL OR stock_actual >= 0);

-- ---- Variantes (acabados): color de la muestra y stock propio (vacío = sin control) ----
ALTER TABLE variantes_producto ADD COLUMN color_hex VARCHAR(7);
ALTER TABLE variantes_producto ADD COLUMN stock_actual INTEGER;
ALTER TABLE variantes_producto ADD CONSTRAINT chk_variantes_stock_actual CHECK (stock_actual IS NULL OR stock_actual >= 0);

-- ---- Reseñas: cuáles se muestran en la Home ----
ALTER TABLE resenas_producto ADD COLUMN mostrar_en_home BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE resenas_producto ADD COLUMN orden_home INTEGER NOT NULL DEFAULT 0;
ALTER TABLE resenas_producto ADD CONSTRAINT chk_resenas_orden_home CHECK (orden_home >= 0);

-- ---- Contenido de la Home de ISADECOR (una tabla por cosa) ----
CREATE TABLE isadecor_hero (
    id UUID PRIMARY KEY,
    unidad_negocio_id UUID NOT NULL,
    titulo VARCHAR(160) NOT NULL,
    subtitulo VARCHAR(300) NOT NULL,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    fecha_creacion TIMESTAMP(6) NOT NULL,
    fecha_actualizacion TIMESTAMP(6) NOT NULL,
    CONSTRAINT uk_isadecor_hero_unidad UNIQUE (unidad_negocio_id),
    CONSTRAINT fk_isadecor_hero_unidad FOREIGN KEY (unidad_negocio_id) REFERENCES unidades_negocio(id)
);

CREATE TABLE isadecor_cinta (
    id UUID PRIMARY KEY,
    unidad_negocio_id UUID NOT NULL,
    texto VARCHAR(160) NOT NULL,
    enlace VARCHAR(300),
    orden INTEGER NOT NULL DEFAULT 0,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    fecha_creacion TIMESTAMP(6) NOT NULL,
    fecha_actualizacion TIMESTAMP(6) NOT NULL,
    CONSTRAINT chk_isadecor_cinta_orden CHECK (orden >= 0),
    CONSTRAINT fk_isadecor_cinta_unidad FOREIGN KEY (unidad_negocio_id) REFERENCES unidades_negocio(id)
);
CREATE INDEX idx_isadecor_cinta_unidad ON isadecor_cinta (unidad_negocio_id, activo, orden);

CREATE TABLE isadecor_promociones (
    id UUID PRIMARY KEY,
    unidad_negocio_id UUID NOT NULL,
    producto_id UUID NOT NULL,
    tipo VARCHAR(10) NOT NULL,
    titulo VARCHAR(140) NOT NULL,
    imagen_url VARCHAR(500) NOT NULL,
    fecha_fin DATE,
    orden INTEGER NOT NULL DEFAULT 0,
    principal BOOLEAN NOT NULL DEFAULT FALSE,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    fecha_creacion TIMESTAMP(6) NOT NULL,
    fecha_actualizacion TIMESTAMP(6) NOT NULL,
    CONSTRAINT chk_isadecor_promociones_tipo CHECK (tipo IN ('DESCUENTO', 'NUEVO')),
    CONSTRAINT chk_isadecor_promociones_orden CHECK (orden >= 0),
    CONSTRAINT fk_isadecor_promociones_unidad FOREIGN KEY (unidad_negocio_id) REFERENCES unidades_negocio(id),
    CONSTRAINT fk_isadecor_promociones_producto FOREIGN KEY (producto_id) REFERENCES productos(id)
);
CREATE INDEX idx_isadecor_promociones_unidad ON isadecor_promociones (unidad_negocio_id, activo, orden);

CREATE TABLE isadecor_campana (
    id UUID PRIMARY KEY,
    unidad_negocio_id UUID NOT NULL,
    etiqueta VARCHAR(60) NOT NULL,
    titulo VARCHAR(160) NOT NULL,
    descripcion VARCHAR(400) NOT NULL,
    texto_boton VARCHAR(60) NOT NULL,
    enlace VARCHAR(300) NOT NULL,
    imagen_url VARCHAR(500) NOT NULL,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    fecha_creacion TIMESTAMP(6) NOT NULL,
    fecha_actualizacion TIMESTAMP(6) NOT NULL,
    CONSTRAINT fk_isadecor_campana_unidad FOREIGN KEY (unidad_negocio_id) REFERENCES unidades_negocio(id)
);
CREATE INDEX idx_isadecor_campana_unidad ON isadecor_campana (unidad_negocio_id, activo);

CREATE TABLE isadecor_servicios_empresa (
    id UUID PRIMARY KEY,
    unidad_negocio_id UUID NOT NULL,
    etiqueta VARCHAR(80) NOT NULL,
    titulo VARCHAR(160) NOT NULL,
    descripcion VARCHAR(400) NOT NULL,
    texto_boton VARCHAR(60) NOT NULL,
    enlace VARCHAR(300) NOT NULL,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    fecha_creacion TIMESTAMP(6) NOT NULL,
    fecha_actualizacion TIMESTAMP(6) NOT NULL,
    CONSTRAINT uk_isadecor_servicios_unidad UNIQUE (unidad_negocio_id),
    CONSTRAINT fk_isadecor_servicios_unidad FOREIGN KEY (unidad_negocio_id) REFERENCES unidades_negocio(id)
);

CREATE TABLE isadecor_servicio_puntos (
    id UUID PRIMARY KEY,
    servicio_id UUID NOT NULL,
    texto VARCHAR(160) NOT NULL,
    orden INTEGER NOT NULL DEFAULT 0,
    fecha_creacion TIMESTAMP(6) NOT NULL,
    fecha_actualizacion TIMESTAMP(6) NOT NULL,
    CONSTRAINT chk_isadecor_servicio_puntos_orden CHECK (orden >= 0),
    CONSTRAINT fk_isadecor_servicio_puntos_servicio FOREIGN KEY (servicio_id) REFERENCES isadecor_servicios_empresa(id) ON DELETE CASCADE
);
CREATE INDEX idx_isadecor_servicio_puntos_servicio ON isadecor_servicio_puntos (servicio_id, orden);

-- ---- Secciones de productos de la Home (Elige tu acabado / Fáciles de instalar) ----
CREATE TABLE isadecor_secciones_productos (
    id UUID PRIMARY KEY,
    unidad_negocio_id UUID NOT NULL,
    clave VARCHAR(10) NOT NULL,
    titulo VARCHAR(120) NOT NULL,
    texto_enlace VARCHAR(80) NOT NULL,
    categoria_enlace VARCHAR(150),
    modo VARCHAR(12) NOT NULL DEFAULT 'AUTOMATICO',
    ventana_dias INTEGER NOT NULL DEFAULT 30,
    limite INTEGER NOT NULL DEFAULT 8,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    fecha_creacion TIMESTAMP(6) NOT NULL,
    fecha_actualizacion TIMESTAMP(6) NOT NULL,
    CONSTRAINT uk_isadecor_secciones_clave UNIQUE (unidad_negocio_id, clave),
    CONSTRAINT chk_isadecor_secciones_clave CHECK (clave IN ('TIENDA', 'FACILES')),
    CONSTRAINT chk_isadecor_secciones_modo CHECK (modo IN ('AUTOMATICO', 'MANUAL', 'MIXTO')),
    CONSTRAINT chk_isadecor_secciones_ventana CHECK (ventana_dias BETWEEN 1 AND 365),
    CONSTRAINT chk_isadecor_secciones_limite CHECK (limite BETWEEN 1 AND 24),
    CONSTRAINT fk_isadecor_secciones_unidad FOREIGN KEY (unidad_negocio_id) REFERENCES unidades_negocio(id)
);

CREATE TABLE isadecor_seccion_fijados (
    id UUID PRIMARY KEY,
    seccion_id UUID NOT NULL,
    producto_id UUID NOT NULL,
    orden INTEGER NOT NULL DEFAULT 0,
    CONSTRAINT uk_isadecor_seccion_fijado UNIQUE (seccion_id, producto_id),
    CONSTRAINT chk_isadecor_seccion_fijados_orden CHECK (orden >= 0),
    CONSTRAINT fk_isadecor_seccion_fijados_seccion FOREIGN KEY (seccion_id) REFERENCES isadecor_secciones_productos(id) ON DELETE CASCADE,
    CONSTRAINT fk_isadecor_seccion_fijados_producto FOREIGN KEY (producto_id) REFERENCES productos(id)
);

CREATE TABLE isadecor_seccion_excluidos (
    id UUID PRIMARY KEY,
    seccion_id UUID NOT NULL,
    producto_id UUID NOT NULL,
    CONSTRAINT uk_isadecor_seccion_excluido UNIQUE (seccion_id, producto_id),
    CONSTRAINT fk_isadecor_seccion_excluidos_seccion FOREIGN KEY (seccion_id) REFERENCES isadecor_secciones_productos(id) ON DELETE CASCADE,
    CONSTRAINT fk_isadecor_seccion_excluidos_producto FOREIGN KEY (producto_id) REFERENCES productos(id)
);

-- ---- Historial de stock ----
CREATE TABLE movimientos_stock (
    id UUID PRIMARY KEY,
    producto_id UUID NOT NULL,
    variante_id UUID,
    tipo VARCHAR(12) NOT NULL,
    cantidad INTEGER NOT NULL,
    stock_resultante INTEGER,
    motivo VARCHAR(200),
    pedido_id UUID,
    usuario VARCHAR(120),
    fecha TIMESTAMP(6) NOT NULL,
    CONSTRAINT chk_movimientos_stock_tipo CHECK (tipo IN ('ENTRADA', 'SALIDA', 'AJUSTE', 'PEDIDO', 'CANCELACION')),
    CONSTRAINT fk_movimientos_stock_producto FOREIGN KEY (producto_id) REFERENCES productos(id),
    CONSTRAINT fk_movimientos_stock_variante FOREIGN KEY (variante_id) REFERENCES variantes_producto(id),
    CONSTRAINT fk_movimientos_stock_pedido FOREIGN KEY (pedido_id) REFERENCES pedidos(id)
);
CREATE INDEX idx_movimientos_stock_producto ON movimientos_stock (producto_id, fecha);
