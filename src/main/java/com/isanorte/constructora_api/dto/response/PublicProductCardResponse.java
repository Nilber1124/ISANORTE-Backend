package com.isanorte.constructora_api.dto.response;

import java.math.BigDecimal;
import java.util.List;

import com.isanorte.constructora_api.enums.EstadoDisponibilidad;

/** Información mínima para una tarjeta del catálogo público. */
public record PublicProductCardResponse(
        String nombre, String sku, String slug, String resumen, String descripcion,
        BigDecimal precioBase, BigDecimal precioAnterior, BigDecimal descuentoPorcentaje,
        EstadoDisponibilidad disponibilidad, PublicProductImageResponse imagen,
        List<PublicProductCategoryResponse> categorias) {
}
