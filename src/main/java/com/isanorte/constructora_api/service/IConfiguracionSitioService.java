package com.isanorte.constructora_api.service;

import java.util.UUID;

import com.isanorte.constructora_api.dto.request.ConfiguracionSitioRequest;
import com.isanorte.constructora_api.model.ConfiguracionSitio;

public interface IConfiguracionSitioService extends IGenericService<ConfiguracionSitio, UUID> {

    ConfiguracionSitio create(ConfiguracionSitioRequest request);
}
