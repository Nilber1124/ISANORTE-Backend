package com.isanorte.constructora_api.dto.request;

import com.isanorte.constructora_api.enums.TipoMovimientoStock;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

/** Ajuste manual de stock: ENTRADA suma, SALIDA resta y AJUSTE fija el stock exacto. */
public record AjusteStockRequest(
        @NotNull TipoMovimientoStock tipo,
        @NotNull @PositiveOrZero Integer cantidad,
        @Size(max = 200) String motivo) {
}
