package com.isanorte.constructora_api.dto.request;

import jakarta.validation.constraints.NotBlank;

public record ServicioRequest(
    @NotBlank String nombre,
    @NotBlank String slug,
    String resumen,
    @NotBlank String descripcion,
    String icono,
    String imagenUrl,
    Boolean activo,
    Boolean destacado,
    Integer orden) {
}
