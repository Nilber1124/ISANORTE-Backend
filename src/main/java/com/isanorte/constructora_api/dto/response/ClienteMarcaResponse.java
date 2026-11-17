package com.isanorte.constructora_api.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record ClienteMarcaResponse(
        UUID id,
        String nombre,
        String logoUrl,
        String enlace,
        Boolean activo,
        Integer orden,
        LocalDateTime fechaCreacion,
        LocalDateTime fechaActualizacion) {
}
