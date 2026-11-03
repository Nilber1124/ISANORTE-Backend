package com.isanorte.constructora_api.service;

import java.util.List;
import java.util.UUID;

import com.isanorte.constructora_api.dto.request.DireccionClienteRequest;
import com.isanorte.constructora_api.dto.response.DireccionClienteResponse;

public interface IDireccionClienteService {

    List<DireccionClienteResponse> findByCliente(UUID clienteId);

    DireccionClienteResponse create(UUID clienteId, DireccionClienteRequest request);

    DireccionClienteResponse update(UUID clienteId, UUID direccionId, DireccionClienteRequest request);

    void delete(UUID clienteId, UUID direccionId);
}
