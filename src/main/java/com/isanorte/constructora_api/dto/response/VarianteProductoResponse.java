package com.isanorte.constructora_api.dto.response;

import java.math.BigDecimal;
import java.util.UUID;

public record VarianteProductoResponse(
        UUID id, String sku, String nombre, String descripcion, BigDecimal precio,
        Boolean disponible, String imagenUrl, Integer orden) {
}
