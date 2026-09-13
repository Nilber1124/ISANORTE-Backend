package com.isanorte.constructora_api.service;

import java.util.List;
import java.util.UUID;

import com.isanorte.constructora_api.dto.request.ConfiguracionSitioRequest;
import com.isanorte.constructora_api.dto.request.ConfiguracionSitioUpdateRequest;
import com.isanorte.constructora_api.dto.response.ConfiguracionSitioResponse;
import com.isanorte.constructora_api.model.ConfiguracionSitio;

public interface IConfiguracionSitioService extends IGenericService<ConfiguracionSitio, UUID> {

    ConfiguracionSitio create(ConfiguracionSitioRequest request);

    List<ConfiguracionSitioResponse> findAllResponse();

    ConfiguracionSitioResponse findByIdResponse(UUID id);

    ConfiguracionSitioResponse createResponse(ConfiguracionSitioRequest request);

    ConfiguracionSitioResponse update(UUID id, ConfiguracionSitioUpdateRequest request);
}
