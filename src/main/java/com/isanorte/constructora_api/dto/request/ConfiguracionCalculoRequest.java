package com.isanorte.constructora_api.dto.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record ConfiguracionCalculoRequest(
        @NotNull Boolean habilitada,
        @Size(max = 100) String etiquetaEntrada,
        @Size(max = 50) String unidadEntrada,
        @NotNull @Positive BigDecimal coberturaPorUnidad,
        @Size(max = 50) String unidadVenta,
        @Size(max = 255) String textoAyuda) {
}
