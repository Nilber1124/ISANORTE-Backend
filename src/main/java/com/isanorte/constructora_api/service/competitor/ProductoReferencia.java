package com.isanorte.constructora_api.service.competitor;

import java.math.BigDecimal;
import java.util.Map;

public record ProductoReferencia(String nombre, String categoria, BigDecimal precio,
        String moneda, String unidadPrecio, Map<String, String> caracteristicas) {}
