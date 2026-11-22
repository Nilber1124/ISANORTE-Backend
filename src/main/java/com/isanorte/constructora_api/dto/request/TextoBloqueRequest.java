package com.isanorte.constructora_api.dto.request;

import jakarta.validation.constraints.Size;

/** Textos de cabecera de un bloque; un campo vacío vuelve al texto por defecto de la Home. */
public record TextoBloqueRequest(
        @Size(max = 150) String etiqueta,
        @Size(max = 300) String titulo,
        @Size(max = 600) String subtitulo) {
}
