package com.isanorte.constructora_api.dto.response;

import java.util.UUID;

public record AccionLandingResponse(UUID id, String texto, String enlace, Integer orden, Boolean activo) {
}
