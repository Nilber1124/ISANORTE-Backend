package com.isanorte.constructora_api.dto.response;

import java.util.UUID;

public record SeccionProductoItemResponse(UUID productoId, String nombre, String slug, Integer orden) {
}
