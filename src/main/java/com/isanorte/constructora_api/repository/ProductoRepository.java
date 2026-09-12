package com.isanorte.constructora_api.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.isanorte.constructora_api.model.Producto;

@Repository
public interface ProductoRepository extends IGenericRepository<Producto, UUID> {

    Optional<Producto> findBySlug(String slug);

    Optional<Producto> findBySku(String sku);

    boolean existsBySlug(String slug);

    boolean existsBySku(String sku);
}
