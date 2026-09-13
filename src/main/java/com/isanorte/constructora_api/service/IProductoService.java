package com.isanorte.constructora_api.service;

import java.util.List;
import java.util.UUID;

import com.isanorte.constructora_api.dto.request.ProductoRequest;
import com.isanorte.constructora_api.dto.request.EstadoPublicacionRequest;
import com.isanorte.constructora_api.dto.request.ProductoUpdateRequest;
import com.isanorte.constructora_api.dto.response.ProductoResponse;
import com.isanorte.constructora_api.model.Producto;

public interface IProductoService extends IGenericService<Producto, UUID> {

    Producto findBySlug(String slug);

    Producto findBySku(String sku);

    Producto create(ProductoRequest request);

    List<ProductoResponse> findAllResponse();

    ProductoResponse findByIdResponse(UUID id);

    ProductoResponse findBySlugResponse(String slug);

    ProductoResponse findBySkuResponse(String sku);

    List<ProductoResponse> findPublishedResponses();

    ProductoResponse findPublishedBySlugResponse(String slug);

    ProductoResponse findPublishedBySkuResponse(String sku);

    ProductoResponse createResponse(ProductoRequest request);

    ProductoResponse update(UUID id, ProductoUpdateRequest request);

    ProductoResponse updateEstado(UUID id, EstadoPublicacionRequest request);
}
