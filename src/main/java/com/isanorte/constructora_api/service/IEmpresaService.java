package com.isanorte.constructora_api.service;

import java.util.List;
import java.util.UUID;

import com.isanorte.constructora_api.dto.request.EmpresaRequest;
import com.isanorte.constructora_api.dto.request.EmpresaUpdateRequest;
import com.isanorte.constructora_api.dto.response.EmpresaResponse;
import com.isanorte.constructora_api.model.Empresa;

public interface IEmpresaService extends IGenericService<Empresa, UUID> {

    Empresa create(EmpresaRequest request);

    List<EmpresaResponse> findAllResponse();

    EmpresaResponse findByIdResponse(UUID id);

    EmpresaResponse createResponse(EmpresaRequest request);

    EmpresaResponse update(UUID id, EmpresaUpdateRequest request);
}
