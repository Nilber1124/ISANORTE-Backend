package com.isanorte.constructora_api.dto.response;

import java.util.UUID;

public record EspecificacionProductoResponse(UUID id, String clave, String valor, String grupo, Integer orden) {
}
