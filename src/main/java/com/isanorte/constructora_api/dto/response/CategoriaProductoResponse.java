package com.isanorte.constructora_api.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record CategoriaProductoResponse(
    UUID id,
    String nombre,
    String slug,
    String descripcion,
    String imagenUrl,
    Boolean activo,
    Integer orden,
    UnidadNegocioResumen unidadNegocio,
    LocalDateTime fechaCreacion,
    LocalDateTime fechaActualizacion) {

    public record UnidadNegocioResumen(UUID id, String nombre, String slug) {
    }
}
