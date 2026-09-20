package com.isanorte.constructora_api.service;

import java.util.List;
import java.util.UUID;

import com.isanorte.constructora_api.dto.request.ServicioRequest;
import com.isanorte.constructora_api.dto.request.ActivoRequest;
import com.isanorte.constructora_api.dto.request.ServicioUpdateRequest;
import com.isanorte.constructora_api.dto.response.ServicioResponse;
import com.isanorte.constructora_api.model.Servicio;
import com.isanorte.constructora_api.dto.request.BeneficioServicioRequest;
import com.isanorte.constructora_api.dto.response.BeneficioServicioResponse;

public interface IServicioService extends IGenericService<Servicio, UUID> {

    Servicio findBySlug(String slug);

    Servicio create(ServicioRequest request);

    List<ServicioResponse> findAllResponse();

    ServicioResponse findByIdResponse(UUID id);

    ServicioResponse findBySlugResponse(String slug);

    List<ServicioResponse> findActiveResponses();

    ServicioResponse findActiveBySlugResponse(String slug);

    ServicioResponse createResponse(ServicioRequest request);

    ServicioResponse update(UUID id, ServicioUpdateRequest request);

    ServicioResponse updateActivo(UUID id, ActivoRequest request);
    BeneficioServicioResponse createBeneficio(UUID servicioId, BeneficioServicioRequest request);
    BeneficioServicioResponse updateBeneficio(UUID servicioId, UUID beneficioId, BeneficioServicioRequest request);
    void deleteBeneficio(UUID servicioId, UUID beneficioId);
}
