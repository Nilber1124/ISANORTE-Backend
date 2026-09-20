package com.isanorte.constructora_api.dto.response;

import java.util.UUID;

public record HeroSceneResponse(UUID id, String imagenUrl, String alt, Integer orden, Boolean activo) {
}
