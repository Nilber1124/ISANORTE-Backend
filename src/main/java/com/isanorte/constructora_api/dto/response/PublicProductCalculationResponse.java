package com.isanorte.constructora_api.dto.response;

import java.math.BigDecimal;

/** Configuración de calculadora que puede presentar el detalle público. */
public record PublicProductCalculationResponse(
        Boolean habilitada, String etiquetaEntrada, String unidadEntrada,
        BigDecimal coberturaPorUnidad, String unidadVenta, String textoAyuda) {
}
