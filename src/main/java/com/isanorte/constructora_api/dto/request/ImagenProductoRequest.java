package com.isanorte.constructora_api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ImagenProductoRequest(
        @NotBlank @Size(max = 500) String url,
        @Size(max = 200) String altText,
        @NotNull Boolean esPrincipal,
        @NotNull Integer orden) {
}
