package com.isanorte.constructora_api.dto.response;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

public record ProyectoResponse(
    UUID id,
    String nombre,
    String slug,
    String cliente,
    String ubicacion,
    String fechaProyecto,
    String descripcion,
    String imagenUrl,
    String imagenAlt,
    Boolean destacado,
    Boolean activo,
    Integer orden,
    Set<ServicioResumen> servicios,
    LocalDateTime fechaCreacion,
    LocalDateTime fechaActualizacion) {

    public record ServicioResumen(UUID id, String nombre, String slug) {
    }
}
