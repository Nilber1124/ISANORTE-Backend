package com.isanorte.constructora_api.dto.request;

import com.isanorte.constructora_api.enums.OrdenCatalogoProducto;

public record PublicProductCatalogQuery(
        int page,
        int size,
        String categoriaSlug,
        String texto,
        boolean soloDescuento,
        boolean soloDisponible,
        boolean soloDestacados,
        OrdenCatalogoProducto orden) {
}
