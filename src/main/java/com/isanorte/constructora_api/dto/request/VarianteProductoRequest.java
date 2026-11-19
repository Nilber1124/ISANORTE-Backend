package com.isanorte.constructora_api.dto.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record VarianteProductoRequest(
        @NotBlank @Size(max = 100) String sku,
        @NotBlank @Size(max = 150) String nombre,
        @Size(max = 500) String descripcion,
        @PositiveOrZero BigDecimal precio,
        @NotNull Boolean disponible,
        @Size(max = 500) String imagenUrl,
        @NotNull Integer orden,
        @Pattern(regexp = "^#[0-9a-fA-F]{6}$") String colorHex) {
}
