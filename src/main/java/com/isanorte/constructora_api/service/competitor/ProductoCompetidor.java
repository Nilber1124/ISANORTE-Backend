package com.isanorte.constructora_api.service.competitor;

import java.math.BigDecimal;
import java.util.Map;

public record ProductoCompetidor(String empresa, String nombre, String url,
        BigDecimal precio, BigDecimal precioAnterior, String moneda, String unidadPrecio,
        BigDecimal cantidadPorPresentacion, Map<String, String> caracteristicas, BigDecimal similitud) {
    public ProductoCompetidor conSimilitud(BigDecimal valor) {
        return new ProductoCompetidor(empresa, nombre, url, precio, precioAnterior, moneda,
                unidadPrecio, cantidadPorPresentacion, caracteristicas, valor);
    }
}
