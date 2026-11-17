package com.isanorte.constructora_api.dto.response;

import java.util.List;

/** Contenido público que construye la confianza del sitio: solo elementos activos y ordenados. */
public record PublicConfianzaResponse(
        List<Faq> faqs,
        List<Miembro> equipo,
        List<Credencial> credenciales,
        List<Testimonio> testimonios,
        List<Cliente> clientes,
        List<Garantia> garantias,
        List<Paso> pasos,
        List<ImagenProyecto> galeria) {

    public record ImagenProyecto(String proyectoSlug, String url, String alt, String etiqueta) {
    }

    public record Faq(String pregunta, String respuesta, String ambito) {
    }

    public record Miembro(String nombre, String cargo, String especialidad, String biografia,
            String fotoUrl, String fotoAlt) {
    }

    public record Credencial(String nombre, String entidad, String tipo, String codigo, Integer anio,
            String imagenUrl, String imagenAlt) {
    }

    public record Testimonio(String autor, String cargo, String texto, String proyectoSlug,
            String fotoUrl, String fotoAlt) {
    }

    public record Cliente(String nombre, String logoUrl, String enlace) {
    }

    public record Garantia(String titulo, String texto, boolean enHero) {
    }

    public record Paso(String titulo, String texto) {
    }
}
