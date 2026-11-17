package com.isanorte.constructora_api.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record ProyectoGaleriaResponse(
        UUID id,
        String proyectoSlug,
        String url,
        String alt,
        String etiqueta,
        Boolean activo,
        Integer orden,
        LocalDateTime fechaCreacion,
        LocalDateTime fechaActualizacion) {
}
