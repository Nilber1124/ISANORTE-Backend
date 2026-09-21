package com.isanorte.constructora_api.dto.response;

/** Imagen pública de un producto, sin identificadores internos. */
public record PublicProductImageResponse(String url, String alt, Boolean esPrincipal, Integer orden) {
}
