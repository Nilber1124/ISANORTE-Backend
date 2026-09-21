package com.isanorte.constructora_api.dto.response;

/** Especificación técnica presentable de un producto público. */
public record PublicProductSpecificationResponse(String clave, String valor, String grupo, Integer orden) {
}
