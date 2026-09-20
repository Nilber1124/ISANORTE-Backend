package com.isanorte.constructora_api.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

import com.isanorte.constructora_api.enums.EstadoSolicitudContacto;

public record SolicitudContactoResponse(
        UUID id, String nombre, String email, String telefono, String empresa, String mensaje,
        EstadoSolicitudContacto estado, LocalDateTime fechaCreacion, LocalDateTime fechaActualizacion) {
}
