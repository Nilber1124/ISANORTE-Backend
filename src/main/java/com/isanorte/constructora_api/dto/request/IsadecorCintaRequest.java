package com.isanorte.constructora_api.dto.request;

import jakarta.validation.constraints.*;
import java.util.UUID;

public record IsadecorCintaRequest(
        @NotNull UUID unidadNegocioId,
        @NotBlank @Size(max = 160) String texto,
        @Size(max = 300) @Pattern(regexp = "^(/|https?://).*") String enlace,
        @NotNull @Min(0) Integer orden,
        @NotNull Boolean activo) {
}
