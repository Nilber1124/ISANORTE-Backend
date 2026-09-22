package com.isanorte.constructora_api.service;

import com.isanorte.constructora_api.dto.request.ComparacionPrecioRequest;
import com.isanorte.constructora_api.dto.response.ComparacionPrecioResponse;

public interface IComparacionPrecioService {
    ComparacionPrecioResponse comparar(String clave, String unidadSlug, String productoSlug,
            ComparacionPrecioRequest request);
}

