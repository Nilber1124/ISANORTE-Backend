package com.isanorte.constructora_api.service;

import java.util.List;
import java.util.UUID;

import com.isanorte.constructora_api.dto.request.UnidadNegocioRequest;
import com.isanorte.constructora_api.model.UnidadNegocio;

public interface IUnidadNegocioService extends IGenericService<UnidadNegocio, UUID> {

    UnidadNegocio findBySlug(String slug);

    List<UnidadNegocio> findByActivoTrueOrderByOrdenAsc();

    UnidadNegocio create(UnidadNegocioRequest request);
}
