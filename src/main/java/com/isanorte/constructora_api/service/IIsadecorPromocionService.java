package com.isanorte.constructora_api.service;

import java.util.List;
import java.util.UUID;

import com.isanorte.constructora_api.dto.request.ActivoRequest;
import com.isanorte.constructora_api.dto.request.IsadecorPromocionRequest;
import com.isanorte.constructora_api.dto.response.IsadecorPromocionResponse;

public interface IIsadecorPromocionService {

    List<IsadecorPromocionResponse> findAll();

    IsadecorPromocionResponse findById(UUID id);

    IsadecorPromocionResponse create(IsadecorPromocionRequest request);

    IsadecorPromocionResponse update(UUID id, IsadecorPromocionRequest request);

    IsadecorPromocionResponse updateActivo(UUID id, ActivoRequest request);

    void delete(UUID id);
}
