package com.isanorte.constructora_api.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record MiembroEquipoResponse(
        UUID id,
        String nombre,
        String cargo,
        String especialidad,
        String biografia,
        String fotoUrl,
        String fotoAlt,
        Boolean activo,
        Integer orden,
        LocalDateTime fechaCreacion,
        LocalDateTime fechaActualizacion) {
}
