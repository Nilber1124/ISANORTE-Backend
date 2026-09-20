package com.isanorte.constructora_api.dto.response;

import java.util.UUID;

import com.isanorte.constructora_api.enums.TipoRecursoUnidadNegocio;

public record RecursoUnidadNegocioResponse(
        UUID id, TipoRecursoUnidadNegocio tipo, String url, String alt, String etiqueta,
        Integer orden, Boolean activo) {
}
