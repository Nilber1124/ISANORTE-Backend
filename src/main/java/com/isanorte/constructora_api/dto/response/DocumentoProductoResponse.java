package com.isanorte.constructora_api.dto.response;

import java.util.UUID;

import com.isanorte.constructora_api.enums.TipoDocumentoProducto;

public record DocumentoProductoResponse(
        UUID id, String titulo, String url, TipoDocumentoProducto tipo, String formato, Long tamanoBytes) {
}
