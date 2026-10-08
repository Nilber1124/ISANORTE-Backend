package com.isanorte.constructora_api.service;

import java.util.List;
import java.util.UUID;

import com.isanorte.constructora_api.dto.request.ActivoRequest;
import com.isanorte.constructora_api.dto.request.AnuncioRequest;
import com.isanorte.constructora_api.dto.response.AnuncioResponse;
import com.isanorte.constructora_api.enums.DestinoAnuncio;

public interface IAnuncioService {
    List<AnuncioResponse> findAll();
    AnuncioResponse findById(UUID id);
    AnuncioResponse create(AnuncioRequest request);
    AnuncioResponse update(UUID id, AnuncioRequest request);
    AnuncioResponse updateActivo(UUID id, ActivoRequest request);
    void delete(UUID id);
    List<AnuncioResponse> findPublicos(DestinoAnuncio destino);
}

