package com.isanorte.constructora_api.dto.request;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

import com.isanorte.constructora_api.enums.EstadoDisponibilidad;
import com.isanorte.constructora_api.enums.EstadoPublicacion;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record ProductoUpdateRequest(
    @NotBlank String sku,
    @NotBlank String nombre,
    @NotBlank String slug,
    String resumen,
    @NotBlank String descripcion,
    @PositiveOrZero BigDecimal precioBase,
    @PositiveOrZero BigDecimal precioAnterior,
    @DecimalMin("0.0") @DecimalMax("100.0") BigDecimal descuentoPorcentaje,
    @NotNull EstadoDisponibilidad disponibilidad,
    @NotNull Boolean destacado,
    Boolean retiroEnTienda,
    @NotNull EstadoPublicacion estado,
    String tituloSeo,
    String descripcionSeo,
    @NotNull UUID unidadNegocioId,
    @NotNull Set<@NotNull UUID> categoriaIds) {
}
