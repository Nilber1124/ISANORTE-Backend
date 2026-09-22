package com.isanorte.constructora_api.dto.request;

import java.util.Set;
import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record ProyectoUpdateRequest(
    @NotBlank String nombre,
    @NotBlank String slug,
    String cliente,
    String ubicacion,
    String fechaProyecto,
    @NotBlank String descripcion,
    @Size(max = 500) String imagenUrl,
    @Size(max = 300) String imagenAlt,
    @NotNull Boolean destacado,
    @NotNull Boolean activo,
    @NotNull @PositiveOrZero Integer orden,
    @NotNull Set<@NotNull UUID> servicioIds) {
}
