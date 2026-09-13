package com.isanorte.constructora_api.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.isanorte.constructora_api.enums.TipoImagenProyecto;

public record ProyectoResponse(
    UUID id,
    String nombre,
    String slug,
    String cliente,
    String ubicacion,
    String fechaProyecto,
    String descripcion,
    Boolean destacado,
    Boolean activo,
    Set<ServicioResumen> servicios,
    List<ImagenResponse> imagenes,
    LocalDateTime fechaCreacion,
    LocalDateTime fechaActualizacion) {

    public record ServicioResumen(UUID id, String nombre, String slug) {
    }

    public record ImagenResponse(
        UUID id,
        String url,
        String titulo,
        String descripcion,
        TipoImagenProyecto tipo,
        Boolean esPrincipal,
        Integer orden) {
    }
}
