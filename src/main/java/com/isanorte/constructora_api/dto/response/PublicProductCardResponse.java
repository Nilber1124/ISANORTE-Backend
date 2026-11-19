package com.isanorte.constructora_api.dto.response;

import java.math.BigDecimal;
import java.util.List;

import com.isanorte.constructora_api.enums.EstadoDisponibilidad;

import com.isanorte.constructora_api.enums.EstadoStock;
import com.isanorte.constructora_api.enums.ModalidadVenta;
import com.isanorte.constructora_api.enums.UnidadVenta;

/** Información mínima para una tarjeta del catálogo público. */
public record PublicProductCardResponse(
        String nombre, String sku, String slug, String resumen, String descripcion,
        BigDecimal precioBase, BigDecimal precioAnterior, BigDecimal descuentoPorcentaje,
        EstadoDisponibilidad disponibilidad, Boolean destacado, Integer ordenDestacado,
        Boolean retiroEnTienda, PublicProductImageResponse imagen,
        List<PublicProductCategoryResponse> categorias,
        ModalidadVenta modalidad, UnidadVenta unidadVenta, String plazoEntrega, Double valoracion, Long resenasCantidad,
        List<IsadecorHomeResponse.Acabado> acabados, EstadoStock estadoStock, Integer quedan, Boolean facilInstalacion) {
}
