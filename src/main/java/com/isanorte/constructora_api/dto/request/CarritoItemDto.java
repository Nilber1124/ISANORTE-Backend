package com.isanorte.constructora_api.dto.request;

import java.math.BigDecimal;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

/** Ítem del carrito tal como lo muestra la tienda. El precio es referencial: la cotización se recalcula en el servidor. */
public record CarritoItemDto(
        @NotBlank @Size(max = 200) String productoSlug,
        @NotBlank @Size(max = 200) String productoNombre,
        @NotBlank @Size(max = 100) String productoSku,
        @Size(max = 500) String imagenUrl,
        @Size(max = 300) String imagenAlt,
        @Valid VarianteDto variante,
        @NotNull @Min(1) @Max(999_999) Integer cantidad,
        @PositiveOrZero BigDecimal precioUnitario) {

    public record VarianteDto(
            @NotBlank @Size(max = 100) String sku,
            @NotBlank @Size(max = 200) String nombre,
            @PositiveOrZero BigDecimal precio) {
    }
}
