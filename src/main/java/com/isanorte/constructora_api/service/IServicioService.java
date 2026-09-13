package com.isanorte.constructora_api.service;

import java.util.UUID;

import com.isanorte.constructora_api.dto.request.ServicioRequest;
import com.isanorte.constructora_api.model.Servicio;

public interface IServicioService extends IGenericService<Servicio, UUID> {

    Servicio findBySlug(String slug);

    Servicio create(ServicioRequest request);
}
