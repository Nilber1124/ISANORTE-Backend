package com.isanorte.constructora_api.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record IsadecorServicioEmpresaResponse(
        UUID id,
        UUID unidadNegocioId,
        String etiqueta,
        String titulo,
        String descripcion,
        String textoBoton,
        String enlace,
        Boolean activo,
        LocalDateTime fechaCreacion,
        LocalDateTime fechaActualizacion) {
}
