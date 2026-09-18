package com.isanorte.constructora_api.dto.response;

import java.math.BigDecimal;
import java.util.UUID;

public record ConfiguracionCalculoResponse(
        UUID id, Boolean habilitada, String etiquetaEntrada, String unidadEntrada,
        BigDecimal coberturaPorUnidad, String unidadVenta, String textoAyuda) {
}
