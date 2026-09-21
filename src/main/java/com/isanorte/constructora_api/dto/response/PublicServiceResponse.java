package com.isanorte.constructora_api.dto.response;

import java.util.List;

/** Servicio editorial público usado exclusivamente por la página Servicios. */
public record PublicServiceResponse(
        String nombre, String slug, String etiqueta, String resumen, String descripcion,
        String imagenUrl, String imagenAlt, Integer orden,
        List<PublicServiceBenefitResponse> beneficios) {
}
