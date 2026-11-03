package com.isanorte.constructora_api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record DireccionClienteRequest(
        @Size(max = 120) String alias,
        @NotBlank @Size(max = 255) String calle,
        @NotBlank @Size(max = 120) String distrito,
        @NotBlank @Size(max = 120) String ciudad,
        @Size(max = 255) String referencia,
        @NotBlank @Size(max = 30) String telefonoContacto,
        @NotNull Boolean principal) {
}
