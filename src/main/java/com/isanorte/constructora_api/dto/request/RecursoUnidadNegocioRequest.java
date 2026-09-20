package com.isanorte.constructora_api.dto.request;

import com.isanorte.constructora_api.enums.TipoRecursoUnidadNegocio;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record RecursoUnidadNegocioRequest(
        @NotNull TipoRecursoUnidadNegocio tipo,
        @NotBlank @Size(max = 500) String url,
        @Size(max = 300) String alt,
        @Size(max = 120) String etiqueta,
        @NotNull @PositiveOrZero Integer orden,
        @NotNull Boolean activo) {
}
