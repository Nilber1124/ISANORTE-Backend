package com.isanorte.constructora_api.service;

import java.util.List;
import java.util.UUID;

import com.isanorte.constructora_api.dto.request.ActivoRequest;
import com.isanorte.constructora_api.dto.request.ProyectoGaleriaRequest;
import com.isanorte.constructora_api.dto.response.ProyectoGaleriaResponse;

public interface IProyectoGaleriaService {

    List<ProyectoGaleriaResponse> findAll();

    ProyectoGaleriaResponse findById(UUID id);

    ProyectoGaleriaResponse create(ProyectoGaleriaRequest request);

    ProyectoGaleriaResponse update(UUID id, ProyectoGaleriaRequest request);

    ProyectoGaleriaResponse updateActivo(UUID id, ActivoRequest request);

    void delete(UUID id);
}
