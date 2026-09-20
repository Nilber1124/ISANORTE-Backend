package com.isanorte.constructora_api.dto.request;

import java.util.List;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ContenidoPaginaUpdateRequest(
        @Size(max = 180) String eyebrow,
        @Size(max = 220) String titulo,
        String introduccion,
        String descripcion,
        @Size(max = 500) String imagenUrl,
        @Size(max = 300) String imagenAlt,
        @Size(max = 500) String imagenFondoUrl,
        @NotNull Boolean activo,
        List<@jakarta.validation.constraints.NotBlank @Size(max = 80) String> tags) {
}
