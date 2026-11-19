package com.isanorte.constructora_api.service;

import java.util.List;
import java.util.UUID;

import com.isanorte.constructora_api.dto.request.ActivoRequest;
import com.isanorte.constructora_api.dto.request.IsadecorCampanaRequest;
import com.isanorte.constructora_api.dto.response.IsadecorCampanaResponse;

public interface IIsadecorCampanaService {

    List<IsadecorCampanaResponse> findAll();

    IsadecorCampanaResponse findById(UUID id);

    IsadecorCampanaResponse create(IsadecorCampanaRequest request);

    IsadecorCampanaResponse update(UUID id, IsadecorCampanaRequest request);

    IsadecorCampanaResponse updateActivo(UUID id, ActivoRequest request);

    void delete(UUID id);
}
