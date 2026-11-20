package com.isanorte.constructora_api.dto.response;

/** Acabado (variante) disponible en el catálogo, con su color y en cuántos productos publicados aparece. */
public record PublicProductCatalogAcabadoResponse(String nombre, String colorHex, Long cantidad) {
}
