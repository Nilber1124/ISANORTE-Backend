package com.isanorte.constructora_api.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record IsadecorHeroResponse(
        UUID id,
        UUID unidadNegocioId,
        String titulo,
        String subtitulo,
        Boolean activo,
        LocalDateTime fechaCreacion,
        LocalDateTime fechaActualizacion) {
}
