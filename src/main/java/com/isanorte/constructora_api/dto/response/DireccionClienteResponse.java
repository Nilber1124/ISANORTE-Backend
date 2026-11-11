package com.isanorte.constructora_api.dto.response;

import java.util.UUID;

public record DireccionClienteResponse(
        UUID id,
        String alias,
        String calle,
        String distrito,
        String ciudad,
        String referencia,
        String telefonoContacto,
        Boolean principal) {
}
