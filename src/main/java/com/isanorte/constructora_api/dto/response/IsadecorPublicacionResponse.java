package com.isanorte.constructora_api.dto.response;

import com.isanorte.constructora_api.enums.OrigenPublicacion;
import com.isanorte.constructora_api.enums.RedPublicacion;

import java.time.LocalDateTime;
import java.util.UUID;

public record IsadecorPublicacionResponse(
        UUID id,
        UUID unidadNegocioId,
        RedPublicacion red,
        String url,
        String titulo,
        String miniaturaUrl,
        OrigenPublicacion origen,
        Integer orden,
        Boolean activo,
        LocalDateTime fechaCreacion,
        LocalDateTime fechaActualizacion) {
}
