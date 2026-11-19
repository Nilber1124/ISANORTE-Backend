package com.isanorte.constructora_api.service;

import java.util.Optional;

import com.isanorte.constructora_api.enums.RedPublicacion;

/** Obtiene el título y la miniatura de una publicación desde la propia red (oEmbed). */
public interface IPublicacionMetadataProvider {

    record Metadata(String titulo, String miniaturaUrl) {
    }

    /** Vacío si la red no responde o la publicación no es pública; nunca lanza. */
    Optional<Metadata> fetch(RedPublicacion red, String url);
}
