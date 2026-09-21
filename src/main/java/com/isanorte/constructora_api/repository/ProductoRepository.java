package com.isanorte.constructora_api.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.EntityGraph;

import com.isanorte.constructora_api.enums.EstadoPublicacion;
import com.isanorte.constructora_api.model.Producto;

@Repository
public interface ProductoRepository extends IGenericRepository<Producto, UUID> {

    Optional<Producto> findBySlug(String slug);

    Optional<Producto> findBySku(String sku);

    List<Producto> findByEstado(EstadoPublicacion estado);

    Optional<Producto> findBySlugAndEstado(String slug, EstadoPublicacion estado);

    @EntityGraph(attributePaths = { "imagenes", "categorias", "categorias.unidadNegocio" })
    List<Producto> findByUnidadNegocioIdAndEstadoOrderByNombreAscIdAsc(UUID unidadNegocioId, EstadoPublicacion estado);

    @EntityGraph(attributePaths = { "categorias", "categorias.unidadNegocio" })
    Optional<Producto> findByUnidadNegocioIdAndSlugAndEstado(
            UUID unidadNegocioId, String slug, EstadoPublicacion estado);

    boolean existsByCategorias_IdAndUnidadNegocio_IdNot(UUID categoriaId, UUID unidadNegocioId);

    Optional<Producto> findBySkuAndEstado(String sku, EstadoPublicacion estado);

    boolean existsBySlug(String slug);

    boolean existsBySku(String sku);

    boolean existsBySlugAndIdNot(String slug, UUID id);

    boolean existsBySkuAndIdNot(String sku, UUID id);
}
