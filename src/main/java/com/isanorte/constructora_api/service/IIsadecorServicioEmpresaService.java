package com.isanorte.constructora_api.service;

import java.util.List;
import java.util.UUID;

import com.isanorte.constructora_api.dto.request.ActivoRequest;
import com.isanorte.constructora_api.dto.request.IsadecorServicioEmpresaRequest;
import com.isanorte.constructora_api.dto.response.IsadecorServicioEmpresaResponse;

public interface IIsadecorServicioEmpresaService {

    List<IsadecorServicioEmpresaResponse> findAll();

    IsadecorServicioEmpresaResponse findById(UUID id);

    IsadecorServicioEmpresaResponse create(IsadecorServicioEmpresaRequest request);

    IsadecorServicioEmpresaResponse update(UUID id, IsadecorServicioEmpresaRequest request);

    IsadecorServicioEmpresaResponse updateActivo(UUID id, ActivoRequest request);

    void delete(UUID id);
}
