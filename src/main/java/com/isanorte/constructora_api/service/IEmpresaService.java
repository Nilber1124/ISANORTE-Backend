package com.isanorte.constructora_api.service;

import java.util.UUID;

import com.isanorte.constructora_api.dto.request.EmpresaRequest;
import com.isanorte.constructora_api.model.Empresa;

public interface IEmpresaService extends IGenericService<Empresa, UUID> {

    Empresa create(EmpresaRequest request);
}
