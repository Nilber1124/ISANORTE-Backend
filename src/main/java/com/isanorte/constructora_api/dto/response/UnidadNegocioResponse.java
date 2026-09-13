package com.isanorte.constructora_api.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record UnidadNegocioResponse(
    UUID id,
    String nombre,
    String slug,
    String descripcion,
    String icono,
    String imagenUrl,
    Boolean activo,
    Integer orden,
    EmpresaResumen empresa,
    LocalDateTime fechaCreacion,
    LocalDateTime fechaActualizacion) {

    public record EmpresaResumen(UUID id, String nombreComercial) {
    }
}
