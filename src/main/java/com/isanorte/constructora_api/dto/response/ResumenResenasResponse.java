package com.isanorte.constructora_api.dto.response;

import java.math.BigDecimal;
import java.util.List;

public record ResumenResenasResponse(
        BigDecimal promedio,
        long total,
        List<Distribucion> distribucion) {

    public record Distribucion(int calificacion, long cantidad, BigDecimal porcentaje) {
    }
}
