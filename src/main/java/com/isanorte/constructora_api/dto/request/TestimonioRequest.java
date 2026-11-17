package com.isanorte.constructora_api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record TestimonioRequest(
        @NotBlank @Size(max = 150) String autor,
        @Size(max = 150) String cargo,
        @NotBlank String texto,
        @Size(max = 200) String proyectoSlug,
        @Size(max = 500) String fotoUrl,
        @Size(max = 300) String fotoAlt,
        @NotNull Boolean activo,
        @NotNull @PositiveOrZero Integer orden) {
}
