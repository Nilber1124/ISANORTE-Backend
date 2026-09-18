package com.isanorte.constructora_api.dto.response;

import java.util.UUID;

/** Representación pública de un enlace institucional. */
public record RedSocialResponse(
        UUID id,
        String nombre,
        String url,
        String icono,
        Integer orden,
        Boolean activo) {
}
