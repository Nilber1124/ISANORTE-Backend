package com.isanorte.constructora_api.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AdministradorUpdateRequest(
    @NotBlank String nombre,
    String apellido,
    @NotBlank @Email String email,
    String telefono,
    @NotNull Boolean activo) {
}
