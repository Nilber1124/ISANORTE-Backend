package com.isanorte.constructora_api.service;

import java.util.List;
import java.util.UUID;

import com.isanorte.constructora_api.dto.request.ActivoRequest;
import com.isanorte.constructora_api.dto.request.PasoProcesoRequest;
import com.isanorte.constructora_api.dto.response.PasoProcesoResponse;

public interface IPasoProcesoService {

    List<PasoProcesoResponse> findAll();

    PasoProcesoResponse findById(UUID id);

    PasoProcesoResponse create(PasoProcesoRequest request);

    PasoProcesoResponse update(UUID id, PasoProcesoRequest request);

    PasoProcesoResponse updateActivo(UUID id, ActivoRequest request);

    void delete(UUID id);
}
