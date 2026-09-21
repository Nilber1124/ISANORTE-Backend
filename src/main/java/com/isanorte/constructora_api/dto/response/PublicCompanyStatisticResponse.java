package com.isanorte.constructora_api.dto.response;

import java.math.BigDecimal;

/** Estadística corporativa pública, sin identidad ni estado administrativo. */
public record PublicCompanyStatisticResponse(
        BigDecimal valor, String prefijo, String sufijo, String etiqueta, Integer orden) {
}
