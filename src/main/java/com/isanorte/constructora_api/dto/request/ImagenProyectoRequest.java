package com.isanorte.constructora_api.dto.request;

import com.isanorte.constructora_api.enums.TipoImagenProyecto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ImagenProyectoRequest(
        @NotBlank @Size(max = 500) String url,
        @Size(max = 150) String titulo,
        @Size(max = 300) String descripcion,
        @Size(max = 300) String alt,
        @NotNull TipoImagenProyecto tipo,
        @NotNull Boolean esPrincipal,
        @NotNull @jakarta.validation.constraints.PositiveOrZero Integer orden) {
}
