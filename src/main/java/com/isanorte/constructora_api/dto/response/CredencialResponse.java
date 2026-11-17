package com.isanorte.constructora_api.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record CredencialResponse(
        UUID id,
        String nombre,
        String entidad,
        String tipo,
        String codigo,
        Integer anio,
        String imagenUrl,
        String imagenAlt,
        Boolean activo,
        Integer orden,
        LocalDateTime fechaCreacion,
        LocalDateTime fechaActualizacion) {
}
