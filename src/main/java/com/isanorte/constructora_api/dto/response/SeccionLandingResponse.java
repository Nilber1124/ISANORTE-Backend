package com.isanorte.constructora_api.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

import com.isanorte.constructora_api.enums.TipoSeccionLanding;

public record SeccionLandingResponse(
    UUID id,
    TipoSeccionLanding tipo,
    String titulo,
    String subtitulo,
    String contenido,
    String imagenUrl,
    String textoBoton,
    String enlaceBoton,
    Integer orden,
    Boolean visible,
    UUID configuracionSitioId,
    LocalDateTime fechaCreacion,
    LocalDateTime fechaActualizacion) {
}
