package com.isanorte.constructora_api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record MiembroEquipoRequest(
        @NotBlank @Size(max = 150) String nombre,
        @NotBlank @Size(max = 150) String cargo,
        @Size(max = 200) String especialidad,
        String biografia,
        @Size(max = 500) String fotoUrl,
        @Size(max = 300) String fotoAlt,
        @NotNull Boolean activo,
        @NotNull @PositiveOrZero Integer orden) {
}
