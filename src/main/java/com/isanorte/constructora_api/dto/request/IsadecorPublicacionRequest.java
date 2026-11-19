package com.isanorte.constructora_api.dto.request;

import com.isanorte.constructora_api.enums.RedPublicacion;

import jakarta.validation.constraints.*;
import java.util.UUID;

/**
 * La miniatura y el título son opcionales: si faltan, se intentan obtener de la propia red al guardar.
 */
public record IsadecorPublicacionRequest(
        @NotNull UUID unidadNegocioId,
        @NotNull RedPublicacion red,
        @NotBlank @Size(max = 500) @Pattern(regexp = "^https://.*") String url,
        @Size(max = 200) String titulo,
        @Size(max = 500) @Pattern(regexp = "^https://.*") String miniaturaUrl,
        @NotNull @Min(0) Integer orden,
        @NotNull Boolean activo) {
}
