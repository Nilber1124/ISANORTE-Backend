package com.isanorte.constructora_api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ServicioUpdateRequest(
    @NotBlank String nombre,
    @NotBlank String slug,
    String resumen,
    @NotBlank String descripcion,
    String icono,
    String imagenUrl,
    @NotNull Boolean activo,
    @NotNull Boolean destacado,
    @NotNull Integer orden) {
}
