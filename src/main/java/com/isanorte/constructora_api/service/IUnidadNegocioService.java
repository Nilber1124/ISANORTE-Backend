package com.isanorte.constructora_api.service;

import java.util.List;
import java.util.UUID;

import com.isanorte.constructora_api.dto.request.UnidadNegocioRequest;
import com.isanorte.constructora_api.dto.request.ActivoRequest;
import com.isanorte.constructora_api.dto.request.UnidadNegocioUpdateRequest;
import com.isanorte.constructora_api.dto.response.UnidadNegocioResponse;
import com.isanorte.constructora_api.model.UnidadNegocio;
import com.isanorte.constructora_api.dto.request.RecursoUnidadNegocioRequest;
import com.isanorte.constructora_api.dto.response.RecursoUnidadNegocioResponse;

public interface IUnidadNegocioService extends IGenericService<UnidadNegocio, UUID> {

    UnidadNegocio findBySlug(String slug);

    List<UnidadNegocio> findByActivoTrueOrderByOrdenAsc();

    UnidadNegocio create(UnidadNegocioRequest request);

    List<UnidadNegocioResponse> findAllResponse();

    UnidadNegocioResponse findByIdResponse(UUID id);

    UnidadNegocioResponse findBySlugResponse(String slug);

    List<UnidadNegocioResponse> findActiveResponses();

    UnidadNegocioResponse findActiveBySlugResponse(String slug);

    UnidadNegocioResponse createResponse(UnidadNegocioRequest request);

    UnidadNegocioResponse update(UUID id, UnidadNegocioUpdateRequest request);

    UnidadNegocioResponse updateActivo(UUID id, ActivoRequest request);
    RecursoUnidadNegocioResponse createRecurso(UUID unidadId, RecursoUnidadNegocioRequest request);
    RecursoUnidadNegocioResponse updateRecurso(UUID unidadId, UUID recursoId, RecursoUnidadNegocioRequest request);
    void deleteRecurso(UUID unidadId, UUID recursoId);
}
