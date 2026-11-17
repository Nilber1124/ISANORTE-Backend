package com.isanorte.constructora_api.service;

import java.util.List;
import java.util.UUID;

import com.isanorte.constructora_api.dto.request.ActivoRequest;
import com.isanorte.constructora_api.dto.request.PoliticaRequest;
import com.isanorte.constructora_api.dto.response.PoliticaResponse;

public interface IPoliticaService {

    List<PoliticaResponse> findAll();

    PoliticaResponse findById(UUID id);

    PoliticaResponse create(PoliticaRequest request);

    PoliticaResponse update(UUID id, PoliticaRequest request);

    PoliticaResponse updateActivo(UUID id, ActivoRequest request);

    void delete(UUID id);
}
