package com.isanorte.constructora_api.service;

import java.util.List;
import java.util.UUID;

import com.isanorte.constructora_api.dto.request.SeoPaginaRequest;
import com.isanorte.constructora_api.dto.request.SeoPaginaUpdateRequest;
import com.isanorte.constructora_api.dto.response.SeoPaginaResponse;

public interface ISeoPaginaService {
    List<SeoPaginaResponse> findAll();
    SeoPaginaResponse findById(UUID id);
    SeoPaginaResponse create(SeoPaginaRequest request);
    SeoPaginaResponse update(UUID id, SeoPaginaUpdateRequest request);
}
