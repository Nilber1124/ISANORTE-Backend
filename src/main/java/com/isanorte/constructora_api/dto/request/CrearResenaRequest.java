package com.isanorte.constructora_api.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CrearResenaRequest(
        @Min(1) @Max(5) int calificacion,
        @Size(max = 160) String titulo,
        @NotBlank @Size(max = 4000) String comentario) {
}
