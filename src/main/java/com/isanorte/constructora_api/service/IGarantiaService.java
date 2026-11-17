package com.isanorte.constructora_api.service;

import java.util.List;
import java.util.UUID;

import com.isanorte.constructora_api.dto.request.ActivoRequest;
import com.isanorte.constructora_api.dto.request.GarantiaRequest;
import com.isanorte.constructora_api.dto.response.GarantiaResponse;

public interface IGarantiaService {

    List<GarantiaResponse> findAll();

    GarantiaResponse findById(UUID id);

    GarantiaResponse create(GarantiaRequest request);

    GarantiaResponse update(UUID id, GarantiaRequest request);

    GarantiaResponse updateActivo(UUID id, ActivoRequest request);

    void delete(UUID id);
}
