package com.isanorte.constructora_api.dto.request;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.isanorte.constructora_api.enums.TipoImagenProyecto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ProyectoRequest(
    @NotBlank String nombre,
    @NotBlank String slug,
    String cliente,
    String ubicacion,
    String fechaProyecto,
    @NotBlank String descripcion,
    Boolean destacado,
    Boolean activo,
    Set<@NotNull UUID> servicioIds,
    List<@Valid ImagenRequest> imagenes) {

    public record ImagenRequest(
        @NotBlank String url,
        String titulo,
        String descripcion,
        @NotNull TipoImagenProyecto tipo,
        Boolean esPrincipal,
        Integer orden) {
    }
}
