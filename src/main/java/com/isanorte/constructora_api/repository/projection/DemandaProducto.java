package com.isanorte.constructora_api.repository.projection;

import java.util.UUID;

/** Unidades pedidas o cotizadas de un producto en una ventana de tiempo. */
public interface DemandaProducto {

    UUID getProductoId();

    Long getCantidad();
}
