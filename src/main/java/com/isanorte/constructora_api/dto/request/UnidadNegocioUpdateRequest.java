package com.isanorte.constructora_api.dto.request;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record UnidadNegocioUpdateRequest(
    @NotBlank String nombre,
    @NotBlank String slug,
    String descripcion,
    String icono,
    String imagenUrl,
    @Size(max = 300) String imagenAlt,
    @NotNull Boolean activo,
    @NotNull Boolean destacado,
    @NotNull @PositiveOrZero Integer orden,
    @NotNull UUID empresaId) {
}
