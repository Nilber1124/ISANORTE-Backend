package com.isanorte.constructora_api.service;

import java.util.List;
import java.util.UUID;

import com.isanorte.constructora_api.dto.request.ActivoRequest;
import com.isanorte.constructora_api.dto.request.IsadecorServicioPuntoRequest;
import com.isanorte.constructora_api.dto.response.IsadecorServicioPuntoResponse;

public interface IIsadecorServicioPuntoService {

    List<IsadecorServicioPuntoResponse> findAll();

    IsadecorServicioPuntoResponse findById(UUID id);

    IsadecorServicioPuntoResponse create(IsadecorServicioPuntoRequest request);

    IsadecorServicioPuntoResponse update(UUID id, IsadecorServicioPuntoRequest request);

    void delete(UUID id);
}
