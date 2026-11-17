package com.isanorte.constructora_api.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.NotBlank;

public record EmpresaUpdateRequest(
    @NotBlank String razonSocial,
    @NotBlank String nombreComercial,
    @NotBlank String ruc,
    String direccion,
    String ciudad,
    String telefono,
    String telefonoSecundario,
    @Email String email,
    @Email String emailVentas,
    String whatsapp,
    String horarioAtencion,
    @PositiveOrZero Integer anioFundacion,
    @Size(max = 300) String zonaCobertura,
    @Size(max = 120) String tiempoRespuesta,
    String mision,
    String vision,
    String valores,
    String resumenNosotros) {
}
