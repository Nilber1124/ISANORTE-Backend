package com.isanorte.constructora_api.dto.response;

import java.util.UUID;

import com.isanorte.constructora_api.enums.EstadoStock;

/** Stock resultante de un ajuste; el estado se calcula con el inventario completo del producto. */
public record StockResponse(
        UUID productoId, UUID varianteId, Integer stockActual, Integer stockMinimo, EstadoStock estado, Integer quedan) {
}
