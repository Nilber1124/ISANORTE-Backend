package com.isanorte.constructora_api.service;

import java.util.List;
import java.util.UUID;

import com.isanorte.constructora_api.dto.request.ActivoRequest;
import com.isanorte.constructora_api.dto.request.FaqRequest;
import com.isanorte.constructora_api.dto.response.FaqResponse;

public interface IFaqService {

    List<FaqResponse> findAll();

    FaqResponse findById(UUID id);

    FaqResponse create(FaqRequest request);

    FaqResponse update(UUID id, FaqRequest request);

    FaqResponse updateActivo(UUID id, ActivoRequest request);

    void delete(UUID id);
}
