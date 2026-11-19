package com.isanorte.constructora_api.dto.request;

import java.math.BigDecimal;

import com.isanorte.constructora_api.enums.ModalidadVenta;
import com.isanorte.constructora_api.enums.OrdenCatalogoProducto;

public record PublicProductCatalogQuery(
        int page,
        int size,
        String categoriaSlug,
        String texto,
        boolean soloDescuento,
        boolean soloDisponible,
        boolean soloDestacados,
        OrdenCatalogoProducto orden,
        ModalidadVenta modalidad, Boolean facilInstalacion,
        BigDecimal precioMin, BigDecimal precioMax, String acabado) {

    public PublicProductCatalogQuery {
        if ((precioMin != null && precioMin.signum() < 0) || (precioMax != null && precioMax.signum() < 0)) {
            throw new IllegalArgumentException("Los precios deben ser mayores o iguales a 0");
        }
        if (precioMin != null && precioMax != null && precioMin.compareTo(precioMax) > 0) {
            throw new IllegalArgumentException("precioMin debe ser menor o igual a precioMax");
        }
    }
}
