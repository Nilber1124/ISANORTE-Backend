package com.isanorte.constructora_api.service;

import java.util.List;
import java.util.UUID;

import com.isanorte.constructora_api.dto.request.ResenaHomeRequest;
import com.isanorte.constructora_api.dto.response.ResenaAdminResponse;

public interface IResenaAdminService {

    /** Todas las reseñas: primero las marcadas para la Home y luego las más recientes. */
    List<ResenaAdminResponse> findAll();

    ResenaAdminResponse updateHome(UUID id, ResenaHomeRequest request);
}
