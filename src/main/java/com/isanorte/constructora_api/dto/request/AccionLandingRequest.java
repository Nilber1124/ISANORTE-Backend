package com.isanorte.constructora_api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record AccionLandingRequest(
        @NotBlank @Size(max = 80) String texto,
        @NotBlank @Size(max = 500) String enlace,
        @NotNull @PositiveOrZero Integer orden,
        @NotNull Boolean activo) {
}
