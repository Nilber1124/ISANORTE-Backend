package com.isanorte.constructora_api.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Datos de perfil permitidos. La creación de credenciales se incorporará junto
 * con la futura capa de seguridad; este DTO nunca acepta hashes de contraseña.
 */
public record AdministradorRequest(
    @NotBlank String nombre,
    String apellido,
    @NotBlank @Email String email,
    String telefono,
    Boolean activo) {
}
