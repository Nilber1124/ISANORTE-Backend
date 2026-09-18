package com.isanorte.constructora_api.dto.response;

import java.util.UUID;

public record ImagenProductoResponse(UUID id, String url, String altText, Boolean esPrincipal, Integer orden) {
}
