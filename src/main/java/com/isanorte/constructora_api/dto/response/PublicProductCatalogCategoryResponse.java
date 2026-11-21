package com.isanorte.constructora_api.dto.response;

/**
 * Categoría del catálogo público con el conteo de productos publicados que la usan y su imagen.
 * {@code padreSlug} es el slug de la categoría de la que cuelga (null en las categorías raíz); el conteo
 * de una categoría padre incluye los productos de sus subcategorías.
 */
public record PublicProductCatalogCategoryResponse(
        String nombre, String slug, Long cantidad, String imagenUrl, String padreSlug) {

    public PublicProductCatalogCategoryResponse(String nombre, String slug, Long cantidad, String imagenUrl) {
        this(nombre, slug, cantidad, imagenUrl, null);
    }
}
