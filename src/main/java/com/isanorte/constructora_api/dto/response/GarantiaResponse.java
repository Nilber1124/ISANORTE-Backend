package com.isanorte.constructora_api.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record GarantiaResponse(
        UUID id,
        String titulo,
        String texto,
        Boolean enHero,
        Boolean activo,
        Integer orden,
        LocalDateTime fechaCreacion,
        LocalDateTime fechaActualizacion) {
}
