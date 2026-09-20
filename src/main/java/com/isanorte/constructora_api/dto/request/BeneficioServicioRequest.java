package com.isanorte.constructora_api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record BeneficioServicioRequest(
        @NotBlank @Size(max = 300) String texto,
        @NotNull @PositiveOrZero Integer orden,
        @NotNull Boolean activo) {
}
