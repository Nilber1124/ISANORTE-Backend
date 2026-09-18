package com.isanorte.constructora_api.dto.request;

import com.isanorte.constructora_api.enums.TipoDocumentoProducto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record DocumentoProductoRequest(
        @NotBlank @Size(max = 180) String titulo,
        @NotBlank @Size(max = 500) String url,
        @NotNull TipoDocumentoProducto tipo,
        @Size(max = 20) String formato,
        @PositiveOrZero Long tamanoBytes) {
}
