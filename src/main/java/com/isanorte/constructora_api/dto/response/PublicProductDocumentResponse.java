package com.isanorte.constructora_api.dto.response;

import com.isanorte.constructora_api.enums.TipoDocumentoProducto;

/** Documento descargable público de un producto. */
public record PublicProductDocumentResponse(
        String titulo, String url, TipoDocumentoProducto tipo, String formato, Long tamanoBytes) {
}
