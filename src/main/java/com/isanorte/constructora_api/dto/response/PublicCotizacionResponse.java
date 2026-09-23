package com.isanorte.constructora_api.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.isanorte.constructora_api.enums.CanalCotizacion;
import com.isanorte.constructora_api.enums.EstadoCotizacion;

public record PublicCotizacionResponse(
    String codigo,
    String nombreCliente,
    String emailCliente,
    String telefonoCliente,
    String empresaCliente,
    String ciudad,
    String mensaje,
    CanalCotizacion canal,
    EstadoCotizacion estado,
    BigDecimal totalEstimado,
    List<DetallePublicoResponse> detalles,
    LocalDateTime fechaCreacion) {

    public record DetallePublicoResponse(
        String productoSlug,
        String nombreProducto,
        String sku,
        String varianteSku,
        Integer cantidad,
        BigDecimal precioUnitario,
        BigDecimal subtotal,
        String notas) {
    }
}
