package com.isanorte.constructora_api.dto.response;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import com.isanorte.constructora_api.enums.EstadoComparacionPrecio;

public record ComparacionPrecioResponse(
        String producto, String urlExterna, String dominioExterno, String nombreProductoExterno,
        BigDecimal precioInterno, BigDecimal precioExterno, String monedaInterna, String monedaExterna,
        BigDecimal diferencia, BigDecimal porcentajeDiferencia, boolean comparable,
        EstadoComparacionPrecio estado, String mensaje, OffsetDateTime fechaConsulta) {}

