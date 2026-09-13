package com.isanorte.constructora_api.dto.response;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

public record AdministradorResponse(
    UUID id,
    String nombre,
    String apellido,
    String email,
    String telefono,
    Boolean activo,
    LocalDateTime ultimoAcceso,
    Set<RolResumen> roles,
    LocalDateTime fechaCreacion,
    LocalDateTime fechaActualizacion) {

    public record RolResumen(UUID id, String nombre, String descripcion) {
    }
}
