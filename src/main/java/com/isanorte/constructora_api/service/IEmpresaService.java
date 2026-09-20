package com.isanorte.constructora_api.service;

import java.util.List;
import java.util.UUID;

import com.isanorte.constructora_api.dto.request.EmpresaRequest;
import com.isanorte.constructora_api.dto.request.EmpresaUpdateRequest;
import com.isanorte.constructora_api.dto.request.RedSocialRequest;
import com.isanorte.constructora_api.dto.response.EmpresaResponse;
import com.isanorte.constructora_api.dto.response.RedSocialResponse;
import com.isanorte.constructora_api.model.Empresa;
import com.isanorte.constructora_api.dto.request.EstadisticaEmpresaRequest;
import com.isanorte.constructora_api.dto.response.EstadisticaEmpresaResponse;

public interface IEmpresaService extends IGenericService<Empresa, UUID> {

    Empresa create(EmpresaRequest request);

    List<EmpresaResponse> findAllResponse();

    EmpresaResponse findByIdResponse(UUID id);

    EmpresaResponse createResponse(EmpresaRequest request);

    EmpresaResponse update(UUID id, EmpresaUpdateRequest request);

    RedSocialResponse createRedSocial(UUID empresaId, RedSocialRequest request);

    RedSocialResponse updateRedSocial(UUID empresaId, UUID redSocialId, RedSocialRequest request);

    void deleteRedSocial(UUID empresaId, UUID redSocialId);
    EstadisticaEmpresaResponse createEstadistica(UUID empresaId, EstadisticaEmpresaRequest request);
    EstadisticaEmpresaResponse updateEstadistica(UUID empresaId, UUID estadisticaId, EstadisticaEmpresaRequest request);
    void deleteEstadistica(UUID empresaId, UUID estadisticaId);
}
