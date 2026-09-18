package com.isanorte.constructora_api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record EspecificacionProductoRequest(
        @NotBlank @Size(max = 100) String clave,
        @NotBlank @Size(max = 500) String valor,
        @Size(max = 100) String grupo,
        @NotNull Integer orden) {
}
