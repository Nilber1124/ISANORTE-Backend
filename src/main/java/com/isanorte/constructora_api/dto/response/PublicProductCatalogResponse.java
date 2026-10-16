package com.isanorte.constructora_api.dto.response;

import java.util.List;

/** Catálogo web del dominio Producto, limitado a una Unidad de Negocio pública. */
public record PublicProductCatalogResponse(
        Unidad unidad, List<PublicProductCatalogCategoryResponse> categorias,
        Pagina productos) {

    /** Contexto mínimo de la Unidad, sin duplicar el agregado de página raíz. */
    public record Unidad(String nombre, String slug) {
    }

    /** Página de productos ya filtrada/ordenada por el backend. */
    public record Pagina(
            List<PublicProductCardResponse> contenido, int pagina, int tamano,
            long totalElementos, int totalPaginas) {
    }
}
