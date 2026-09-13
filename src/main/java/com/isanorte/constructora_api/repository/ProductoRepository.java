package com.isanorte.constructora_api.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.isanorte.constructora_api.enums.EstadoPublicacion;
import com.isanorte.constructora_api.model.Producto;

@Repository
public interface ProductoRepository extends IGenericRepository<Producto, UUID> {

    Optional<Producto> findBySlug(String slug);

    Optional<Producto> findBySku(String sku);

    List<Producto> findByEstado(EstadoPublicacion estado);

    Optional<Producto> findBySlugAndEstado(String slug, EstadoPublicacion estado);

    Optional<Producto> findBySkuAndEstado(String sku, EstadoPublicacion estado);

    boolean existsBySlug(String slug);

    boolean existsBySku(String sku);

    boolean existsBySlugAndIdNot(String slug, UUID id);

    boolean existsBySkuAndIdNot(String sku, UUID id);
}
