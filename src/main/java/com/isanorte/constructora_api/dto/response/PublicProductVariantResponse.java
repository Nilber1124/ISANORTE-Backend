package com.isanorte.constructora_api.dto.response;

import java.math.BigDecimal;

/** Variante presentable de un producto público. */
public record PublicProductVariantResponse(
        String sku, String nombre, String descripcion, BigDecimal precio,
        Boolean disponible, String imagenUrl, Integer orden) {
}
