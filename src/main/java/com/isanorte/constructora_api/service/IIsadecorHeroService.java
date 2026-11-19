package com.isanorte.constructora_api.service;

import java.util.List;
import java.util.UUID;

import com.isanorte.constructora_api.dto.request.ActivoRequest;
import com.isanorte.constructora_api.dto.request.IsadecorHeroRequest;
import com.isanorte.constructora_api.dto.response.IsadecorHeroResponse;

public interface IIsadecorHeroService {

    List<IsadecorHeroResponse> findAll();

    IsadecorHeroResponse findById(UUID id);

    IsadecorHeroResponse create(IsadecorHeroRequest request);

    IsadecorHeroResponse update(UUID id, IsadecorHeroRequest request);

    IsadecorHeroResponse updateActivo(UUID id, ActivoRequest request);

    void delete(UUID id);
}
