package com.isanorte.constructora_api.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.isanorte.constructora_api.enums.EstadoPedido;

public record PublicPedidoResponse(
        String codigo,
        String direccionCalle,
        String direccionDistrito,
        String direccionCiudad,
        EstadoPedido estado,
        BigDecimal totalPedido,
        List<DetallePublicoResponse> detalles,
        LocalDateTime fechaCreacion) {

    public record DetallePublicoResponse(
            String productoSlug,
            String nombreProducto,
            String sku,
            String varianteSku,
            Integer cantidad,
            BigDecimal precioUnitario,
            BigDecimal subtotal) {
    }
}
