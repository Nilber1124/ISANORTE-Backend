package com.isanorte.constructora_api.dto.request;

import jakarta.validation.constraints.*;
import java.util.UUID;

public record IsadecorCampanaRequest(
        @NotNull UUID unidadNegocioId,
        @NotBlank @Size(max = 60) String etiqueta,
        @NotBlank @Size(max = 160) String titulo,
        @NotBlank @Size(max = 400) String descripcion,
        @NotBlank @Size(max = 60) String textoBoton,
        @NotBlank @Size(max = 300) @Pattern(regexp = "^(/|https?://).*") String enlace,
        @NotBlank @Size(max = 500) @Pattern(regexp = "^https?://.*") String imagenUrl,
        @NotNull Boolean activo) {
}
