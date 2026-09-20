package com.isanorte.constructora_api.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

import com.isanorte.constructora_api.enums.EstadoSolicitudContacto;

public record SolicitudContactoPublicResponse(UUID id, EstadoSolicitudContacto estado, LocalDateTime fechaCreacion) {
}
