package com.isanorte.constructora_api.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

import com.isanorte.constructora_api.enums.TipoNotificacion;

public record NotificacionResponse(
        UUID id,
        TipoNotificacion tipo,
        String titulo,
        String mensaje,
        String enlace,
        Boolean leida,
        LocalDateTime fechaCreacion) {
}
