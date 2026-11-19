package com.isanorte.constructora_api.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record IsadecorCintaResponse(
        UUID id,
        UUID unidadNegocioId,
        String texto,
        String enlace,
        Integer orden,
        Boolean activo,
        LocalDateTime fechaCreacion,
        LocalDateTime fechaActualizacion) {
}
