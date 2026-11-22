package com.isanorte.constructora_api.dto.response;

import java.math.BigDecimal;

/** Lo que el checkout necesita saber: envío y si el pago es de demostración. */
public record TiendaConfigResponse(BigDecimal costoDelivery, boolean recojoHabilitado, String pasarela,
        boolean pagoSimulado) {
}
