package com.isanorte.constructora_api.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record IsadecorCampanaResponse(
        UUID id,
        UUID unidadNegocioId,
        String etiqueta,
        String titulo,
        String descripcion,
        String textoBoton,
        String enlace,
        String imagenUrl,
        Boolean activo,
        LocalDateTime fechaCreacion,
        LocalDateTime fechaActualizacion) {
}
