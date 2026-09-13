package com.isanorte.constructora_api.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record EmpresaResponse(
    UUID id,
    String razonSocial,
    String nombreComercial,
    String ruc,
    String direccion,
    String ciudad,
    String telefono,
    String telefonoSecundario,
    String email,
    String emailVentas,
    String whatsapp,
    String horarioAtencion,
    String mision,
    String vision,
    String valores,
    String resumenNosotros,
    List<RedSocialResponse> redesSociales,
    LocalDateTime fechaCreacion,
    LocalDateTime fechaActualizacion) {

    public record RedSocialResponse(
        UUID id,
        String nombre,
        String url,
        String icono,
        Integer orden,
        Boolean activo) {
    }
}
