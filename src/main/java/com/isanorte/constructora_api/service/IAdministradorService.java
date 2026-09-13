package com.isanorte.constructora_api.service;

import java.util.List;
import java.util.UUID;

import com.isanorte.constructora_api.dto.request.ActivoRequest;
import com.isanorte.constructora_api.dto.request.AdministradorUpdateRequest;
import com.isanorte.constructora_api.dto.response.AdministradorResponse;
import com.isanorte.constructora_api.model.Administrador;

public interface IAdministradorService extends IGenericService<Administrador, UUID> {

    Administrador findByEmail(String email);

    List<AdministradorResponse> findAllResponse();

    AdministradorResponse findByIdResponse(UUID id);

    AdministradorResponse update(UUID id, AdministradorUpdateRequest request);

    AdministradorResponse updateActivo(UUID id, ActivoRequest request);
}
