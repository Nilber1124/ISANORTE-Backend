package com.isanorte.constructora_api.service;

import java.util.List;
import java.util.UUID;

import com.isanorte.constructora_api.dto.request.SeccionLandingRequest;
import com.isanorte.constructora_api.dto.request.SeccionLandingUpdateRequest;
import com.isanorte.constructora_api.dto.request.VisibleRequest;
import com.isanorte.constructora_api.dto.response.SeccionLandingResponse;
import com.isanorte.constructora_api.dto.request.HeroSceneRequest;
import com.isanorte.constructora_api.dto.request.AccionLandingRequest;
import com.isanorte.constructora_api.dto.response.HeroSceneResponse;
import com.isanorte.constructora_api.dto.response.AccionLandingResponse;
import com.isanorte.constructora_api.model.SeccionLanding;

public interface ISeccionLandingService extends IGenericService<SeccionLanding, UUID> {

    List<SeccionLanding> findByVisibleTrueOrderByOrdenAsc();

    SeccionLanding create(SeccionLandingRequest request);

    List<SeccionLandingResponse> findAllResponse();

    SeccionLandingResponse findByIdResponse(UUID id);

    List<SeccionLandingResponse> findVisibleResponses();

    SeccionLandingResponse createResponse(SeccionLandingRequest request);

    SeccionLandingResponse update(UUID id, SeccionLandingUpdateRequest request);

    SeccionLandingResponse updateVisible(UUID id, VisibleRequest request);

    HeroSceneResponse createEscena(UUID seccionId, HeroSceneRequest request);
    HeroSceneResponse updateEscena(UUID seccionId, UUID escenaId, HeroSceneRequest request);
    void deleteEscena(UUID seccionId, UUID escenaId);
    AccionLandingResponse createAccion(UUID seccionId, AccionLandingRequest request);
    AccionLandingResponse updateAccion(UUID seccionId, UUID accionId, AccionLandingRequest request);
    void deleteAccion(UUID seccionId, UUID accionId);
}
