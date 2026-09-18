package com.isanorte.constructora_api.service;

import java.util.List;
import java.util.UUID;

import com.isanorte.constructora_api.dto.request.ProyectoRequest;
import com.isanorte.constructora_api.dto.request.ActivoRequest;
import com.isanorte.constructora_api.dto.request.ProyectoUpdateRequest;
import com.isanorte.constructora_api.dto.request.ImagenProyectoRequest;
import com.isanorte.constructora_api.dto.response.ImagenProyectoResponse;
import com.isanorte.constructora_api.dto.response.ProyectoResponse;
import com.isanorte.constructora_api.model.Proyecto;

public interface IProyectoService extends IGenericService<Proyecto, UUID> {

    Proyecto findBySlug(String slug);

    Proyecto create(ProyectoRequest request);

    List<ProyectoResponse> findAllResponse();

    ProyectoResponse findByIdResponse(UUID id);

    ProyectoResponse findBySlugResponse(String slug);

    List<ProyectoResponse> findActiveResponses();

    ProyectoResponse findActiveBySlugResponse(String slug);

    ProyectoResponse createResponse(ProyectoRequest request);

    ProyectoResponse update(UUID id, ProyectoUpdateRequest request);

    ProyectoResponse updateActivo(UUID id, ActivoRequest request);

    ImagenProyectoResponse createImagen(UUID proyectoId, ImagenProyectoRequest request);

    ImagenProyectoResponse updateImagen(UUID proyectoId, UUID imagenId, ImagenProyectoRequest request);

    void deleteImagen(UUID proyectoId, UUID imagenId);
}
