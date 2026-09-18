package com.isanorte.constructora_api.dto.request;

import jakarta.validation.constraints.NotBlank;

/** Datos editables de un enlace institucional de una empresa. */
public record RedSocialRequest(
        @NotBlank String nombre,
        @NotBlank String url,
        String icono,
        Integer orden,
        Boolean activo) {
}
