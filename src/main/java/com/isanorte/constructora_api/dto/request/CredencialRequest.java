package com.isanorte.constructora_api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record CredencialRequest(
        @NotBlank @Size(max = 200) String nombre,
        @Size(max = 200) String entidad,
        @NotBlank @Size(max = 30) @Pattern(regexp = "LICENCIA|COLEGIATURA|CERTIFICACION|REGISTRO") String tipo,
        @Size(max = 80) String codigo,
        @PositiveOrZero Integer anio,
        @Size(max = 500) String imagenUrl,
        @Size(max = 300) String imagenAlt,
        @NotNull Boolean activo,
        @NotNull @PositiveOrZero Integer orden) {
}
