package com.isanorte.constructora_api.service;

import java.util.UUID;

import com.isanorte.constructora_api.dto.request.ProductoRequest;
import com.isanorte.constructora_api.model.Producto;

public interface IProductoService extends IGenericService<Producto, UUID> {

    Producto findBySlug(String slug);

    Producto findBySku(String sku);

    Producto create(ProductoRequest request);
}
