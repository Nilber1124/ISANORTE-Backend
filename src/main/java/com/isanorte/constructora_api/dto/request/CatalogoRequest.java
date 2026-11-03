package com.isanorte.constructora_api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record CatalogoRequest(
        @NotBlank @Size(max = 200) String titulo,
        @NotBlank @Size(max = 500) String imagenPortadaUrl,
        @NotBlank @Size(max = 500) String archivoUrl,
        @NotNull @PositiveOrZero Integer orden,
        @NotNull Boolean activo) {
}
