package com.isanorte.constructora_api.dto.response;

import java.util.List;

/** Servicio editorial público usado exclusivamente por la página Servicios. */
/** El id permite al admin saber qué servicio se tocó en la vista previa de la página Servicios. */
public record PublicServiceResponse(
        java.util.UUID id, String nombre, String slug, String etiqueta, String resumen, String descripcion,
        String imagenUrl, String imagenAlt, Integer orden,
        List<PublicServiceBenefitResponse> beneficios) {
}
