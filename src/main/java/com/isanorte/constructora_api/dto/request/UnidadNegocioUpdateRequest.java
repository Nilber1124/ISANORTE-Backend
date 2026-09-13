package com.isanorte.constructora_api.dto.request;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UnidadNegocioUpdateRequest(
    @NotBlank String nombre,
    @NotBlank String slug,
    String descripcion,
    String icono,
    String imagenUrl,
    @NotNull Boolean activo,
    @NotNull Integer orden,
    @NotNull UUID empresaId) {
}
