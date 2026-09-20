package com.isanorte.constructora_api.dto.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record EstadisticaEmpresaRequest(
        @NotNull @DecimalMin("0") BigDecimal valor,
        @Size(max = 10) String prefijo,
        @Size(max = 10) String sufijo,
        @NotBlank @Size(max = 120) String etiqueta,
        @NotNull @PositiveOrZero Integer orden,
        @NotNull Boolean activo) {
}
