package com.isanorte.constructora_api.service;

import java.util.List;

import com.isanorte.constructora_api.dto.response.IsadecorHomeResponse.Publicacion;

public interface IIsadecorComunidadService {

    /** Todas las publicaciones activas de la unidad, para la página de comunidad. */
    List<Publicacion> findPublicaciones(String siteKey, String unitSlug);
}
