package com.isanorte.constructora_api.service;

import com.isanorte.constructora_api.dto.response.ComparacionCompetidoresResponse;

public interface IComparacionCompetidoresService {
    ComparacionCompetidoresResponse comparar(String clave, String unidadSlug, String productoSlug);
}
