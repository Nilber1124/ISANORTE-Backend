package com.isanorte.constructora_api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record ProyectoGaleriaRequest(
        @NotBlank @Size(max = 200) String proyectoSlug,
        @NotBlank @Size(max = 500) String url,
        @Size(max = 300) String alt,
        @NotBlank @Size(max = 20) @Pattern(regexp = "ANTES|PROCESO|FINAL|DETALLE") String etiqueta,
        @NotNull Boolean activo,
        @NotNull @PositiveOrZero Integer orden) {
}
