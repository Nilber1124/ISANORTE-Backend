package com.isanorte.constructora_api.service;

import java.util.List;
import java.util.UUID;

import com.isanorte.constructora_api.dto.request.ActivoRequest;
import com.isanorte.constructora_api.dto.request.IsadecorPublicacionRequest;
import com.isanorte.constructora_api.dto.response.IsadecorPublicacionResponse;

public interface IIsadecorPublicacionService {

    List<IsadecorPublicacionResponse> findAll();

    IsadecorPublicacionResponse findById(UUID id);

    IsadecorPublicacionResponse create(IsadecorPublicacionRequest request);

    IsadecorPublicacionResponse update(UUID id, IsadecorPublicacionRequest request);

    IsadecorPublicacionResponse updateActivo(UUID id, ActivoRequest request);

    void delete(UUID id);
}
