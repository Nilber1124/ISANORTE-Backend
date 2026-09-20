package com.isanorte.constructora_api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record HeroSceneRequest(
        @NotBlank @Size(max = 500) String imagenUrl,
        @Size(max = 300) String alt,
        @NotNull @PositiveOrZero Integer orden,
        @NotNull Boolean activo) {
}
