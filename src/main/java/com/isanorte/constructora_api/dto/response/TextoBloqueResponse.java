package com.isanorte.constructora_api.dto.response;

import java.util.UUID;

public record TextoBloqueResponse(UUID id, String clave, String etiqueta, String titulo, String subtitulo) {
}
