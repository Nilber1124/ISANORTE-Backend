package com.isanorte.constructora_api.dto.request;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;

public record CategoriaProductoRequest(
    @NotBlank String nombre,
    @NotBlank String slug,
    String descripcion,
    String imagenUrl,
    Boolean activo,
    Integer orden,
    UUID unidadNegocioId) {
}
