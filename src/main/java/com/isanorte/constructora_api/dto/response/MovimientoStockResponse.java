package com.isanorte.constructora_api.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

import com.isanorte.constructora_api.enums.TipoMovimientoStock;

public record MovimientoStockResponse(
        UUID id, UUID varianteId, TipoMovimientoStock tipo, Integer cantidad, Integer stockResultante,
        String motivo, UUID pedidoId, String usuario, LocalDateTime fecha) {
}
