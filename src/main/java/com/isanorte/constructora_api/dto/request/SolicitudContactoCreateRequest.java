package com.isanorte.constructora_api.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SolicitudContactoCreateRequest(
        @NotBlank @Size(max = 150) String nombre,
        @NotBlank @Email @Size(max = 180) String email,
        @NotBlank @Size(max = 30) String telefono,
        @Size(max = 180) String empresa,
        @Size(max = 150) String ubicacion,
        @Size(max = 180) String servicioInteres,
        @NotBlank @Size(max = 5000) String mensaje,
        @AssertTrue(message = "debes autorizar el uso de tus datos para enviar la solicitud") boolean consentimiento) {
}
