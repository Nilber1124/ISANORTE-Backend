package com.isanorte.constructora_api.dto.response;

import java.util.UUID;

import com.isanorte.constructora_api.enums.TipoImagenProyecto;

public record ImagenProyectoResponse(
        UUID id,
        String url,
        String titulo,
        String descripcion,
        TipoImagenProyecto tipo,
        Boolean esPrincipal,
        Integer orden) {
}
