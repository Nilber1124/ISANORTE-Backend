package com.isanorte.constructora_api.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record ClienteResponse(
        UUID id,
        String nombre,
        String apellido,
        String email,
        String telefono,
        LocalDateTime fechaCreacion) {
}
