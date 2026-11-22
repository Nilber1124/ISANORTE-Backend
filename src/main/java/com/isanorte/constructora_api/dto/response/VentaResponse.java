package com.isanorte.constructora_api.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.isanorte.constructora_api.enums.CanalVenta;
import com.isanorte.constructora_api.enums.MetodoPagoVenta;
import com.isanorte.constructora_api.enums.TipoDocumentoCliente;

public record VentaResponse(
        UUID id,
        String codigo,
        CanalVenta canal,
        LocalDate fechaVenta,
        String clienteNombre,
        String clienteTelefono,
        TipoDocumentoCliente tipoDocumento,
        String documento,
        String razonSocial,
        MetodoPagoVenta metodoPago,
        Boolean pagado,
        BigDecimal total,
        String notas,
        Boolean anulada,
        Boolean stockDescontado,
        List<DetalleVentaResponse> detalles,
        LocalDateTime fechaCreacion) {

    public record DetalleVentaResponse(
            UUID id,
            UUID productoId,
            UUID varianteId,
            String nombre,
            String sku,
            Integer cantidad,
            BigDecimal precioUnitario,
            BigDecimal subtotal) {
    }
}
