package com.isanorte.constructora_api.repository;

import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.isanorte.constructora_api.model.VarianteProducto;

@Repository
public interface VarianteProductoRepository extends IGenericRepository<VarianteProducto, UUID> {

    boolean existsBySku(String sku);

    boolean existsBySkuAndIdNot(String sku, UUID id);
}
