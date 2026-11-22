package com.isanorte.constructora_api.dto.response;

import java.util.List;
import java.util.UUID;

/** Contenido público que construye la confianza del sitio: solo elementos activos y ordenados. */
public record PublicConfianzaResponse(
        List<Faq> faqs,
        List<Miembro> equipo,
        List<Credencial> credenciales,
        List<Testimonio> testimonios,
        List<Garantia> garantias,
        List<Paso> pasos,
        List<ImagenProyecto> galeria,
        List<Politica> politicas,
        List<TextoBloque> textos) {

    public record Politica(String slug, String titulo, String contenido) {
    }

    public record ImagenProyecto(String proyectoSlug, String url, String alt, String etiqueta) {
    }

    /** El id permite al admin saber qué pregunta se tocó en la vista previa de la landing. */
    public record Faq(UUID id, String pregunta, String respuesta, String ambito) {
    }

    public record Miembro(String nombre, String cargo, String especialidad, String biografia,
            String fotoUrl, String fotoAlt) {
    }

    public record Credencial(String nombre, String entidad, String tipo, String codigo, Integer anio,
            String imagenUrl, String imagenAlt) {
    }

    /** El id permite al admin saber qué testimonio se tocó en la vista previa de la landing. */
    public record Testimonio(UUID id, String autor, String cargo, String texto, String proyectoSlug,
            String fotoUrl, String fotoAlt) {
    }

    /** El id permite al admin saber qué garantía se editó al tocarla en la vista previa. */
    public record Garantia(UUID id, String titulo, String texto, boolean enHero) {
    }

    /** El id permite al admin saber qué paso se tocó en la vista previa de la landing. */
    /** Cabecera editable de un bloque de la Home; sin fila, la Home usa su texto por defecto. */
    public record TextoBloque(String clave, String etiqueta, String titulo, String subtitulo) {
    }

    public record Paso(UUID id, String titulo, String texto) {
    }
}
