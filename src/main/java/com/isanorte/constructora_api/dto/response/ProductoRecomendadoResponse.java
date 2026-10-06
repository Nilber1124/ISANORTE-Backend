package com.isanorte.constructora_api.dto.response;

import java.math.BigDecimal;

public record ProductoRecomendadoResponse(
        String nombre,
        String slug,
        BigDecimal precioBase,
        PublicProductImageResponse imagen,
        PublicProductCategoryResponse categoria,
        BigDecimal calificacionPromedio,
        long cantidadResenas) {
}
