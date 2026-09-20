package com.isanorte.constructora_api.service;

import java.util.List;
import java.util.UUID;

import com.isanorte.constructora_api.dto.request.ContenidoPaginaRequest;
import com.isanorte.constructora_api.dto.request.ContenidoPaginaUpdateRequest;
import com.isanorte.constructora_api.dto.response.ContenidoPaginaResponse;

public interface IContenidoPaginaService {
    List<ContenidoPaginaResponse> findAll();
    ContenidoPaginaResponse findById(UUID id);
    ContenidoPaginaResponse create(ContenidoPaginaRequest request);
    ContenidoPaginaResponse update(UUID id, ContenidoPaginaUpdateRequest request);
}
