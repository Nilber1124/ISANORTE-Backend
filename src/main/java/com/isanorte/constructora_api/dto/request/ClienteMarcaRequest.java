package com.isanorte.constructora_api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record ClienteMarcaRequest(
        @NotBlank @Size(max = 150) String nombre,
        @Size(max = 500) String logoUrl,
        @Size(max = 300) String enlace,
        @NotNull Boolean activo,
        @NotNull @PositiveOrZero Integer orden) {
}
