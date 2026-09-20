package com.isanorte.constructora_api.dto.response;

import java.math.BigDecimal;
import java.util.UUID;

public record EstadisticaEmpresaResponse(
        UUID id, BigDecimal valor, String prefijo, String sufijo, String etiqueta,
        Integer orden, Boolean activo) {
}
