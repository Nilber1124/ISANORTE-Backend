package com.isanorte.constructora_api.repository.projection;

import java.util.UUID;

/** Promedio y cantidad de reseñas de un producto. */
public interface ResumenResena {

    UUID getProductoId();

    Double getPromedio();

    Long getCantidad();
}
