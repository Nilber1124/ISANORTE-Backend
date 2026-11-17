package com.isanorte.constructora_api.dto.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.NotBlank;

public record EmpresaRequest(
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
    @DecimalMin("-90.0") @DecimalMax("90.0") Double latitud,
    @DecimalMin("-180.0") @DecimalMax("180.0") Double longitud,
    String mision,
    String vision,
    String valores,
    String resumenNosotros,
    List<@Valid RedSocialRequest> redesSociales) {
}
