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

    boolean existsByCategoriaPadre_Id(UUID padreId);

    boolean existsBySlugAndIdNot(String slug, UUID id);

    List<CategoriaProducto> findByActivoTrueOrderByOrdenAsc();

    /**
     * Conteo de productos publicados por categoría pública de una unidad, replicando la regla
     * de visibilidad usada en el catálogo (activa y, o global, o propia de la unidad). No aplica
     * los filtros activos del catálogo a propósito: el facet siempre refleja el catálogo completo.
     */
    @Query("""
            SELECT new com.isanorte.constructora_api.dto.response.PublicProductCatalogCategoryResponse(
                c.nombre, c.slug, COUNT(p.id), c.imagenUrl, pa.slug)
            FROM CategoriaProducto c JOIN c.productos p LEFT JOIN c.categoriaPadre pa
            WHERE p.unidadNegocio.id = :unidadId AND p.estado = :estado
              AND c.activo = true
              AND (c.unidadNegocio IS NULL OR c.unidadNegocio.id = :unidadId)
            GROUP BY c.id, c.nombre, c.slug, c.orden, c.imagenUrl, pa.slug
            ORDER BY c.orden ASC, c.id ASC
            """)
    List<PublicProductCatalogCategoryResponse> findPublicCategoryCounts(
            @Param("unidadId") UUID unidadId, @Param("estado") EstadoPublicacion estado);

    /** Categorías activas, públicas en la unidad, que son padre de otras (candidatas a aparecer sin productos propios). */
    @Query("""
            SELECT DISTINCT pa FROM CategoriaProducto c JOIN c.categoriaPadre pa
            WHERE pa.activo = true AND c.activo = true
              AND (pa.unidadNegocio IS NULL OR pa.unidadNegocio.id = :unidadId)
            ORDER BY pa.orden ASC, pa.id ASC
            """)
    List<CategoriaProducto> findActiveParents(@Param("unidadId") UUID unidadId);

    /** Productos publicados distintos de una categoría y sus subcategorías (conteo de una categoría padre). */
    @Query("""
            SELECT COUNT(DISTINCT p.id) FROM Producto p JOIN p.categorias c
            WHERE p.unidadNegocio.id = :unidadId AND p.estado = :estado
              AND c.activo = true
              AND (c.id = :categoriaId OR c.categoriaPadre.id = :categoriaId)
            """)
    long countPublishedInTree(@Param("categoriaId") UUID categoriaId,
            @Param("unidadId") UUID unidadId, @Param("estado") EstadoPublicacion estado);
}
