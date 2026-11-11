package com.isanorte.constructora_api.dto.request;

import java.util.List;
import java.util.UUID;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

/**
 * Datos para crear un pedido desde el checkout público. Sin nombre/email/teléfono: el pedido exige
 * sesión de cliente, esos datos salen de la cuenta autenticada, no del request.
 * La dirección de envío se toma de {@code direccionId} (dirección guardada) o de los campos
 * {@code direccion*} en línea; se valida en el servicio que venga una u otra.
 */
public record PublicPedidoRequest(
        UUID direccionId,
        @Size(max = 120) String direccionAlias,
        @Size(max = 255) String direccionCalle,
        @Size(max = 120) String direccionDistrito,
        @Size(max = 120) String direccionCiudad,
        @Size(max = 255) String direccionReferencia,
        @Size(max = 30) String direccionTelefonoContacto,
        @Size(max = 20) String ruc,
        @Size(max = 200) String razonSocial,
        @NotEmpty List<@Valid DetallePublicoRequest> detalles) {

    public record DetallePublicoRequest(
            @NotBlank String productoSlug,
            String varianteSku,
            @Positive Integer cantidad) {
    }
}
