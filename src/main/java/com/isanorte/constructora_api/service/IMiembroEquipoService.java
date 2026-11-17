package com.isanorte.constructora_api.service;

import java.util.List;
import java.util.UUID;

import com.isanorte.constructora_api.dto.request.ActivoRequest;
import com.isanorte.constructora_api.dto.request.MiembroEquipoRequest;
import com.isanorte.constructora_api.dto.response.MiembroEquipoResponse;

public interface IMiembroEquipoService {

    List<MiembroEquipoResponse> findAll();

    MiembroEquipoResponse findById(UUID id);

    MiembroEquipoResponse create(MiembroEquipoRequest request);

    MiembroEquipoResponse update(UUID id, MiembroEquipoRequest request);

    MiembroEquipoResponse updateActivo(UUID id, ActivoRequest request);

    void delete(UUID id);
}
