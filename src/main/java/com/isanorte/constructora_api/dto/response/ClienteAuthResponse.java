package com.isanorte.constructora_api.dto.response;

import java.time.Instant;

public record ClienteAuthResponse(
        String token,
        String tipo,
        Instant expiracion,
        ClienteResponse cliente) {
}
