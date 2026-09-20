package com.isanorte.constructora_api.dto.response;

import java.util.UUID;

public record BeneficioServicioResponse(UUID id, String texto, Integer orden, Boolean activo) {
}
