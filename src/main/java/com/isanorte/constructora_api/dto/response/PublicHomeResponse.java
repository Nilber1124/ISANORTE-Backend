package com.isanorte.constructora_api.dto.response;

import java.util.List;

import com.isanorte.constructora_api.enums.RobotsSeo;
import com.isanorte.constructora_api.enums.TipoRecursoUnidadNegocio;
import com.isanorte.constructora_api.enums.TipoSeccionLanding;

public record PublicHomeResponse(
        List<Seccion> secciones, List<Servicio> servicios, List<Proyecto> proyectos,
        UnidadDestacada unidadDestacada, Seo seo) {

    public record Seccion(
            TipoSeccionLanding tipo, String etiqueta, String titulo, String subtitulo,
            String contenido, String imagenUrl, String imagenAlt, String textoBoton,
            String enlaceBoton, Integer orden, List<Escena> escenas, List<Accion> acciones) {
    }

    public record Escena(String imagenUrl, String alt, Integer orden) {
    }

    public record Accion(String texto, String enlace, Integer orden) {
    }

    public record Servicio(
            String nombre, String slug, String resumen, String descripcion,
            String imagenUrl, String imagenAlt, String etiqueta, Integer orden,
            List<String> beneficios) {
    }

    public record Proyecto(
            String nombre, String slug, String ubicacion, String fechaProyecto,
            String descripcion, Integer orden, List<Imagen> imagenes) {
    }

    public record Imagen(String url, String alt, Boolean esPrincipal, Integer orden) {
    }

    public record UnidadDestacada(
            String nombre, String slug, String descripcion, String icono, String imagenUrl,
            String imagenAlt, Integer orden, List<Recurso> recursos) {
    }

    public record Recurso(
            TipoRecursoUnidadNegocio tipo, String url, String alt, String etiqueta, Integer orden) {
    }

    public record Seo(String title, String description, String ogImageUrl, RobotsSeo robots) {
    }
}
