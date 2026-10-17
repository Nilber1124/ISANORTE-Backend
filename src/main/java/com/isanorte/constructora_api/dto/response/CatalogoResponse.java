package com.isanorte.constructora_api.dto.response;

import java.util.UUID;

public record CatalogoResponse(
        UUID id, String titulo, String imagenPortadaUrl, String archivoUrl, Integer orden, Boolean activo) {
}
