package com.isanorte.constructora_api.dto.request;

import jakarta.validation.constraints.NotNull;

/** Anula (o reactiva) una venta; las ventas no se borran. */
public record AnulacionVentaRequest(@NotNull Boolean anulada) {
}
