package com.isanorte.constructora_api.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

/** Reseña vista desde administración, con el producto al que pertenece y su estado en la Home. */
public record ResenaAdminResponse(
        UUID id, String nombreCliente, Integer calificacion, String comentario, Boolean compraVerificada,
        UUID productoId, String productoNombre, LocalDateTime fechaCreacion,
        Boolean mostrarEnHome, Integer ordenHome) {
}
