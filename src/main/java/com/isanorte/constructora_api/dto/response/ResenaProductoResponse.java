package com.isanorte.constructora_api.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record ResenaProductoResponse(
        UUID id,
        String nombreCliente,
        int calificacion,
        String titulo,
        String comentario,
        LocalDateTime fechaCreacion,
        boolean compraVerificada,
        int cantidadUtil) {
}
