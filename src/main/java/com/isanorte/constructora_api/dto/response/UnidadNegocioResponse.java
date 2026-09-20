package com.isanorte.constructora_api.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.List;

public record UnidadNegocioResponse(
    UUID id,
    String nombre,
    String slug,
    String descripcion,
    String icono,
    String imagenUrl,
    String imagenAlt,
    Boolean activo,
    Boolean destacado,
    Integer orden,
    EmpresaResumen empresa,
    List<RecursoUnidadNegocioResponse> recursos,
    LocalDateTime fechaCreacion,
    LocalDateTime fechaActualizacion) {

    public record EmpresaResumen(UUID id, String nombreComercial) {
    }
}
