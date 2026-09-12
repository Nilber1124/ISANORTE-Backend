package com.isanorte.constructora_api.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.isanorte.constructora_api.model.CategoriaProducto;

@Repository
public interface CategoriaProductoRepository extends IGenericRepository<CategoriaProducto, UUID> {

    Optional<CategoriaProducto> findBySlug(String slug);

    boolean existsBySlug(String slug);

    List<CategoriaProducto> findByActivoTrueOrderByOrdenAsc();
}
