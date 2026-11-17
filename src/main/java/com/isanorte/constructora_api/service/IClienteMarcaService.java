package com.isanorte.constructora_api.service;

import java.util.List;
import java.util.UUID;

import com.isanorte.constructora_api.dto.request.ActivoRequest;
import com.isanorte.constructora_api.dto.request.ClienteMarcaRequest;
import com.isanorte.constructora_api.dto.response.ClienteMarcaResponse;

public interface IClienteMarcaService {

    List<ClienteMarcaResponse> findAll();

    ClienteMarcaResponse findById(UUID id);

    ClienteMarcaResponse create(ClienteMarcaRequest request);

    ClienteMarcaResponse update(UUID id, ClienteMarcaRequest request);

    ClienteMarcaResponse updateActivo(UUID id, ActivoRequest request);

    void delete(UUID id);
}
