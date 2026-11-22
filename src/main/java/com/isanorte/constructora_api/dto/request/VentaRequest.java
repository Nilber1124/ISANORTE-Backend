package com.isanorte.constructora_api.dto.request;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import com.isanorte.constructora_api.enums.CanalVenta;
import com.isanorte.constructora_api.enums.MetodoPagoVenta;
import com.isanorte.constructora_api.enums.TipoDocumentoCliente;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

/** Alta o edición de una venta registrada a mano. El total lo calcula el servidor a partir de las líneas. */
public record VentaRequest(
        @NotNull CanalVenta canal,
        @NotNull LocalDate fechaVenta,
        @Size(max = 150) String clienteNombre,
        @Size(max = 30) String clienteTelefono,
        TipoDocumentoCliente tipoDocumento,
        @Size(max = 20) String documento,
        @Size(max = 200) String razonSocial,
        @NotNull MetodoPagoVenta metodoPago,
        @NotNull Boolean pagado,
        @Size(max = 500) String notas,
        @NotEmpty List<@Valid DetalleVentaRequest> detalles) {

    public record DetalleVentaRequest(
            UUID productoId,
            UUID varianteId,
            @NotBlank @Size(max = 180) String nombre,
            @Size(max = 100) String sku,
            @NotNull @Positive Integer cantidad,
            @NotNull @PositiveOrZero BigDecimal precioUnitario) {
    }
}
