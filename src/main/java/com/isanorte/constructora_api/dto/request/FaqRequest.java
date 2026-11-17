package com.isanorte.constructora_api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record FaqRequest(
        @NotBlank @Size(max = 300) String pregunta,
        @NotBlank String respuesta,
        @NotBlank @Size(max = 20) @Pattern(regexp = "HOME|SERVICIOS|CONTACTO|GENERAL") String ambito,
        @NotNull Boolean activo,
        @NotNull @PositiveOrZero Integer orden) {
}
