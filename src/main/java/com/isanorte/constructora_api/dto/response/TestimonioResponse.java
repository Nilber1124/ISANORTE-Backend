package com.isanorte.constructora_api.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record TestimonioResponse(
        UUID id,
        String autor,
        String cargo,
        String texto,
        String proyectoSlug,
        String fotoUrl,
        String fotoAlt,
        Boolean activo,
        Integer orden,
        LocalDateTime fechaCreacion,
        LocalDateTime fechaActualizacion) {
}
