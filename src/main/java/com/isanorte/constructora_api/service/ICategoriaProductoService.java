package com.isanorte.constructora_api.service;

import java.util.List;
import java.util.UUID;

import com.isanorte.constructora_api.dto.request.CategoriaProductoRequest;
import com.isanorte.constructora_api.model.CategoriaProducto;

public interface ICategoriaProductoService extends IGenericService<CategoriaProducto, UUID> {

    CategoriaProducto findBySlug(String slug);

    List<CategoriaProducto> findByActivoTrueOrderByOrdenAsc();

    CategoriaProducto create(CategoriaProductoRequest request);
}
