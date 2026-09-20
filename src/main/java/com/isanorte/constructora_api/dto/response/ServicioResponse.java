package com.isanorte.constructora_api.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.List;

public record ServicioResponse(
    UUID id,
    String nombre,
    String slug,
    String resumen,
    String descripcion,
    String icono,
    String imagenUrl,
    String etiqueta,
    String imagenAlt,
    Boolean activo,
    Boolean destacado,
    Integer orden,
    List<BeneficioServicioResponse> beneficios,
    LocalDateTime fechaCreacion,
    LocalDateTime fechaActualizacion) {
}
