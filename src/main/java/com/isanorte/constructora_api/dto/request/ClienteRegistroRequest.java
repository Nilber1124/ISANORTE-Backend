package com.isanorte.constructora_api.dto.request;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ClienteRegistroRequest(
        @NotBlank @Size(max = 120) String nombre,
        @Size(max = 120) String apellido,
        @NotBlank @Email @Size(max = 150) String email,
        @NotBlank @Size(min = 8, max = 200) String password,
        @Size(max = 30) String telefono,
        @NotNull @AssertTrue(message = "Debes aceptar el tratamiento de tus datos personales") Boolean aceptaTratamientoDatos) {
}
