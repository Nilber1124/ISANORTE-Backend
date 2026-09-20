package com.isanorte.constructora_api.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.List;

import com.isanorte.constructora_api.enums.TipoSeccionLanding;

public record SeccionLandingResponse(
    UUID id,
    TipoSeccionLanding tipo,
    String etiqueta,
    String titulo,
    String subtitulo,
    String contenido,
    String imagenUrl,
    String imagenAlt,
    String textoBoton,
    String enlaceBoton,
    Integer orden,
    Boolean visible,
    UUID configuracionSitioId,
    List<HeroSceneResponse> escenas,
    List<AccionLandingResponse> acciones,
    LocalDateTime fechaCreacion,
    LocalDateTime fechaActualizacion) {
}
