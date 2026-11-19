package com.isanorte.constructora_api.service;

import java.util.List;
import java.util.UUID;

import com.isanorte.constructora_api.dto.request.ActivoRequest;
import com.isanorte.constructora_api.dto.request.IsadecorCintaRequest;
import com.isanorte.constructora_api.dto.response.IsadecorCintaResponse;

public interface IIsadecorCintaService {

    List<IsadecorCintaResponse> findAll();

    IsadecorCintaResponse findById(UUID id);

    IsadecorCintaResponse create(IsadecorCintaRequest request);

    IsadecorCintaResponse update(UUID id, IsadecorCintaRequest request);

    IsadecorCintaResponse updateActivo(UUID id, ActivoRequest request);

    void delete(UUID id);
}
