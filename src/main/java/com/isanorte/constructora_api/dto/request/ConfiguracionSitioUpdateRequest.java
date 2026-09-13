package com.isanorte.constructora_api.dto.request;

public record ConfiguracionSitioUpdateRequest(
    String tituloSitio,
    String descripcionSitio,
    String logoUrl,
    String logoBlancoUrl,
    String faviconUrl,
    String colorPrimario,
    String colorSecundario,
    String textoPiePagina) {
}
