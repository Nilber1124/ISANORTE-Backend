package com.isanorte.constructora_api.dto.response;

import java.util.List;

/** Proyecto editorial público usado exclusivamente por la página Proyectos. */
public record PublicProjectResponse(
        String nombre, String slug, String descripcion, String ubicacion, String fechaProyecto, Integer areaM2,
        Integer plazoMeses, String alcance, String tipoObra, Integer orden,
        List<PublicProjectImageResponse> imagenes, List<PublicProjectServiceResponse> servicios) {
}
