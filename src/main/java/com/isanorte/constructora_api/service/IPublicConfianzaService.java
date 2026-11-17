package com.isanorte.constructora_api.service;

import com.isanorte.constructora_api.dto.response.PublicConfianzaResponse;

public interface IPublicConfianzaService {

    /** Contenido público de confianza del sitio identificado por su clave (404 si no existe). */
    PublicConfianzaResponse findConfianza(String clave);
}
