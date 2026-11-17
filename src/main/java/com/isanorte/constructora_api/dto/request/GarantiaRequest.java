package com.isanorte.constructora_api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record GarantiaRequest(
        @NotBlank @Size(max = 150) String titulo,
        @NotBlank @Size(max = 500) String texto,
        @NotNull Boolean enHero,
        @NotNull Boolean activo,
        @NotNull @PositiveOrZero Integer orden) {
}
