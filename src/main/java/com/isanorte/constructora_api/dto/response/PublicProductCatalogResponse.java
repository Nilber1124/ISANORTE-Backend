package com.isanorte.constructora_api.dto.response;

import java.util.List;

/** Catálogo web del dominio Producto, limitado a una Unidad de Negocio pública. */
public record PublicProductCatalogResponse(
        Unidad unidad, List<PublicProductCategoryResponse> categorias,
        List<PublicProductCardResponse> productos) {

    /** Contexto mínimo de la Unidad, sin duplicar el agregado de página raíz. */
    public record Unidad(String nombre, String slug) {
    }
}
