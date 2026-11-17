package com.isanorte.constructora_api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record PoliticaRequest(
        @NotBlank @Size(max = 150) String titulo,
        @NotBlank String contenido,
        @NotBlank @Size(max = 80) String slug,
        @NotNull Boolean activo,
        @NotNull @PositiveOrZero Integer orden) {
}
