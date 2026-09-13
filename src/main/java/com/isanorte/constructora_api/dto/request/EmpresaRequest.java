package com.isanorte.constructora_api.dto.request;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
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
    String mision,
    String vision,
    String valores,
    String resumenNosotros,
    List<@Valid RedSocialRequest> redesSociales) {

    public record RedSocialRequest(
        @NotBlank String nombre,
        @NotBlank String url,
        String icono,
        Integer orden,
        Boolean activo) {
    }
}
