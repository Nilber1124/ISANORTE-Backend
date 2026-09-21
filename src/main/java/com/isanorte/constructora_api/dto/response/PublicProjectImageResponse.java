package com.isanorte.constructora_api.dto.response;

/** Imagen editorial pública de un proyecto, sin identidad ni auditoría administrativa. */
public record PublicProjectImageResponse(String url, String alt, Boolean esPrincipal, Integer orden) {
}
