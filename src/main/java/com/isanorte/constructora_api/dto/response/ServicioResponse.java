package com.isanorte.constructora_api.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record ServicioResponse(
    UUID id,
    String nombre,
    String slug,
    String resumen,
    String descripcion,
    String icono,
    String imagenUrl,
    Boolean activo,
    Boolean destacado,
    Integer orden,
    LocalDateTime fechaCreacion,
    LocalDateTime fechaActualizacion) {
}
