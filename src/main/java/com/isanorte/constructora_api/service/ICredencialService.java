package com.isanorte.constructora_api.service;

import java.util.List;
import java.util.UUID;

import com.isanorte.constructora_api.dto.request.ActivoRequest;
import com.isanorte.constructora_api.dto.request.CredencialRequest;
import com.isanorte.constructora_api.dto.response.CredencialResponse;

public interface ICredencialService {

    List<CredencialResponse> findAll();

    CredencialResponse findById(UUID id);

    CredencialResponse create(CredencialRequest request);

    CredencialResponse update(UUID id, CredencialRequest request);

    CredencialResponse updateActivo(UUID id, ActivoRequest request);

    void delete(UUID id);
}
