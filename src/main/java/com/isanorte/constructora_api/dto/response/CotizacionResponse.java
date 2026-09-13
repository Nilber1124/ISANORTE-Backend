package com.isanorte.constructora_api.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.isanorte.constructora_api.enums.CanalCotizacion;
import com.isanorte.constructora_api.enums.EstadoCotizacion;

public record CotizacionResponse(
    UUID id,
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
        EstadoCotizacion estadoAnterior,
        EstadoCotizacion estadoNuevo,
        String comentario,
        LocalDateTime fecha) {
    }
}
