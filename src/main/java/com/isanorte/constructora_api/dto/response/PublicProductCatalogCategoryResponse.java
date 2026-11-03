package com.isanorte.constructora_api.dto.response;

/** Categoría del catálogo público con el conteo de productos publicados que la usan. */
public record PublicProductCatalogCategoryResponse(String nombre, String slug, Long cantidad) {
}
