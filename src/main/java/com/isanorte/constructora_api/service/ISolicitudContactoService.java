package com.isanorte.constructora_api.service;

import java.util.List;
import java.util.UUID;

import com.isanorte.constructora_api.dto.request.EstadoSolicitudContactoRequest;
import com.isanorte.constructora_api.dto.request.SolicitudContactoCreateRequest;
import com.isanorte.constructora_api.dto.response.SolicitudContactoPublicResponse;
import com.isanorte.constructora_api.dto.response.SolicitudContactoResponse;
import com.isanorte.constructora_api.enums.EstadoSolicitudContacto;

public interface ISolicitudContactoService {
    SolicitudContactoPublicResponse createPublic(SolicitudContactoCreateRequest request);
    List<SolicitudContactoResponse> findAll(EstadoSolicitudContacto estado);
    SolicitudContactoResponse findById(UUID id);
    SolicitudContactoResponse updateEstado(UUID id, EstadoSolicitudContactoRequest request);
}
