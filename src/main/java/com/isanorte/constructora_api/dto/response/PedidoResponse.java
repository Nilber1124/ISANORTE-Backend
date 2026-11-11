package com.isanorte.constructora_api.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.isanorte.constructora_api.enums.EstadoPedido;

public record PedidoResponse(
        UUID id,
        String codigo,
        UUID clienteId,
        String direccionAlias,
        String direccionCalle,
        String direccionDistrito,
        String direccionCiudad,
        String direccionReferencia,
        String direccionTelefonoContacto,
        String ruc,
        String razonSocial,
        EstadoPedido estado,
        BigDecimal totalPedido,
        Boolean stockDescontado,
        List<DetalleResponse> detalles,
        List<SeguimientoResponse> seguimientos,
        LocalDateTime fechaCreacion,
        LocalDateTime fechaActualizacion) {

    public record DetalleResponse(
            UUID id,
            UUID productoId,
            UUID varianteId,
            String nombreProducto,
            String sku,
            Integer cantidad,
            BigDecimal precioUnitario,
            BigDecimal subtotal,
            String notas) {
    }

    public record SeguimientoResponse(
            UUID id,
            UUID administradorId,
            String administradorNombre,
            EstadoPedido estadoAnterior,
            EstadoPedido estadoNuevo,
            String comentario,
            LocalDateTime fecha) {
    }
}
