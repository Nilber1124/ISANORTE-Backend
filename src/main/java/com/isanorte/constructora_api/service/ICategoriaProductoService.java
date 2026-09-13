package com.isanorte.constructora_api.service;

import java.util.List;
import java.util.UUID;

import com.isanorte.constructora_api.dto.request.ActivoRequest;
import com.isanorte.constructora_api.dto.request.CategoriaProductoRequest;
import com.isanorte.constructora_api.dto.request.CategoriaProductoUpdateRequest;
import com.isanorte.constructora_api.dto.response.CategoriaProductoResponse;
import com.isanorte.constructora_api.model.CategoriaProducto;

public interface ICategoriaProductoService extends IGenericService<CategoriaProducto, UUID> {

    CategoriaProducto findBySlug(String slug);

    List<CategoriaProducto> findByActivoTrueOrderByOrdenAsc();

    CategoriaProducto create(CategoriaProductoRequest request);

    List<CategoriaProductoResponse> findAllResponse();

    CategoriaProductoResponse findByIdResponse(UUID id);

    CategoriaProductoResponse findBySlugResponse(String slug);

    List<CategoriaProductoResponse> findActiveResponses();

    CategoriaProductoResponse findActiveBySlugResponse(String slug);

    CategoriaProductoResponse createResponse(CategoriaProductoRequest request);

    CategoriaProductoResponse update(UUID id, CategoriaProductoUpdateRequest request);

    CategoriaProductoResponse updateActivo(UUID id, ActivoRequest request);
}
