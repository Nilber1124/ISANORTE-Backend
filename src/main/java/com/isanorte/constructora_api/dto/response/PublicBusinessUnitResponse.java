package com.isanorte.constructora_api.dto.response;

import java.util.List;

import com.isanorte.constructora_api.enums.RobotsSeo;
import com.isanorte.constructora_api.enums.TipoRecursoUnidadNegocio;

public record PublicBusinessUnitResponse(
        String nombre, String slug, String descripcion, String icono, String imagenUrl,
        String imagenAlt, List<Recurso> recursos, Seo seo) {

    public record Recurso(
            TipoRecursoUnidadNegocio tipo, String url, String alt, String etiqueta, Integer orden) {
    }

    public record Seo(String title, String description, String ogImageUrl, RobotsSeo robots) {
    }
}
