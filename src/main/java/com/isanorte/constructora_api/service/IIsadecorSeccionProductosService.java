package com.isanorte.constructora_api.service;

import java.util.List;
import java.util.UUID;

import com.isanorte.constructora_api.dto.request.ActivoRequest;
import com.isanorte.constructora_api.dto.request.IsadecorSeccionProductosRequest;
import com.isanorte.constructora_api.dto.response.IsadecorSeccionProductosResponse;

public interface IIsadecorSeccionProductosService {

    List<IsadecorSeccionProductosResponse> findAll();

    IsadecorSeccionProductosResponse findById(UUID id);

    IsadecorSeccionProductosResponse create(IsadecorSeccionProductosRequest request);

    IsadecorSeccionProductosResponse update(UUID id, IsadecorSeccionProductosRequest request);

    IsadecorSeccionProductosResponse updateActivo(UUID id, ActivoRequest request);

    void delete(UUID id);
}
