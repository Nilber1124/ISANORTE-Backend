package com.isanorte.constructora_api.dto.response;

import java.util.List;

import com.isanorte.constructora_api.enums.RobotsSeo;
import com.isanorte.constructora_api.enums.TipoPaginaPublica;

public record PublicPageResponse(Contenido contenido, Seo seo, PublicCompanyAboutResponse empresa) {

    public record Contenido(
            TipoPaginaPublica pagina, String eyebrow, String titulo, String introduccion,
            String descripcion, String imagenUrl, String imagenAlt, String imagenFondoUrl,
            List<String> tags) {
    }

    public record Seo(String title, String description, String ogImageUrl, RobotsSeo robots) {
    }
}
