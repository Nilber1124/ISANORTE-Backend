package com.isanorte.constructora_api.service;

import java.math.BigDecimal;

public interface IScrapingPrecioService {
    PrecioExterno extraer(String url);

    record PrecioExterno(String url, String dominio, String nombreProducto,
            BigDecimal precio, String moneda) {}
}

