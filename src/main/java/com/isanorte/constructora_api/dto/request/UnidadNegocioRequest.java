package com.isanorte.constructora_api.dto.request;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UnidadNegocioRequest(
    @NotBlank String nombre,
    @NotBlank String slug,
    String descripcion,
    String icono,
    String imagenUrl,
    Boolean activo,
    Integer orden,
    @NotNull UUID empresaId) {
}
