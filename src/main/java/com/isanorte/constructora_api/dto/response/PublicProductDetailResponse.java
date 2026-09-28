package com.isanorte.constructora_api.dto.response;

import java.math.BigDecimal;
import java.util.List;

import com.isanorte.constructora_api.enums.EstadoDisponibilidad;

/** Agregado público completo para el detalle de un producto de una Unidad. */
public record PublicProductDetailResponse(
        String nombre, String sku, String slug, String resumen, String descripcion,
        String tituloSeo, String descripcionSeo,
        BigDecimal precioBase, BigDecimal precioAnterior, BigDecimal descuentoPorcentaje,
        EstadoDisponibilidad disponibilidad, Boolean retiroEnTienda, List<PublicProductCategoryResponse> categorias,
        List<PublicProductImageResponse> imagenes, List<PublicProductVariantResponse> variantes,
        List<PublicProductSpecificationResponse> especificaciones,
        List<PublicProductDocumentResponse> documentos,
        PublicProductCalculationResponse configuracionCalculo) {
}
