package com.isanorte.constructora_api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record ServicioRequest(
    @NotBlank String nombre,
    @NotBlank String slug,
    String resumen,
    @NotBlank String descripcion,
    String icono,
    String imagenUrl,
    @Size(max = 180) String etiqueta,
    @Size(max = 300) String imagenAlt,
    Boolean activo,
    Boolean destacado,
    @PositiveOrZero Integer orden) {
}
