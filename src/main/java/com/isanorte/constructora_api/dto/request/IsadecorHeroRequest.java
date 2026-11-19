package com.isanorte.constructora_api.dto.request;

import jakarta.validation.constraints.*;
import java.util.UUID;

public record IsadecorHeroRequest(
        @NotNull UUID unidadNegocioId,
        @NotBlank @Size(max = 160) String titulo,
        @NotBlank @Size(max = 300) String subtitulo,
        @NotNull Boolean activo) {
}
