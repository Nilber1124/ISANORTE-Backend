package com.isanorte.constructora_api.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.isanorte.constructora_api.dto.response.PublicProductCatalogCategoryResponse;
import com.isanorte.constructora_api.enums.EstadoPublicacion;
import com.isanorte.constructora_api.model.CategoriaProducto;

@Repository
public interface CategoriaProductoRepository extends IGenericRepository<CategoriaProducto, UUID> {

    Optional<CategoriaProducto> findBySlug(String slug);

    Optional<CategoriaProducto> findBySlugAndActivoTrue(String slug);

    boolean existsBySlug(String slug);

    boolean existsBySlugAndIdNot(String slug, UUID id);

    List<CategoriaProducto> findByActivoTrueOrderByOrdenAsc();

    /**
     * Conteo de productos publicados por categoría pública de una unidad, replicando la regla
     * de visibilidad usada en el catálogo (activa y, o global, o propia de la unidad). No aplica
     * los filtros activos del catálogo a propósito: el facet siempre refleja el catálogo completo.
     */
    @Query("""
            SELECT new com.isanorte.constructora_api.dto.response.PublicProductCatalogCategoryResponse(
                c.nombre, c.slug, COUNT(p.id))
            FROM CategoriaProducto c JOIN c.productos p
            WHERE p.unidadNegocio.id = :unidadId AND p.estado = :estado
              AND c.activo = true
              AND (c.unidadNegocio IS NULL OR c.unidadNegocio.id = :unidadId)
            GROUP BY c.id, c.nombre, c.slug, c.orden
            ORDER BY c.orden ASC, c.id ASC
            """)
    List<PublicProductCatalogCategoryResponse> findPublicCategoryCounts(
            @Param("unidadId") UUID unidadId, @Param("estado") EstadoPublicacion estado);
}
