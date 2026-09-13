package com.isanorte.constructora_api.dto.request;

import java.util.UUID;

import com.isanorte.constructora_api.enums.TipoSeccionLanding;

import jakarta.validation.constraints.NotNull;

public record SeccionLandingRequest(
    @NotNull UUID configuracionSitioId,
    @NotNull TipoSeccionLanding tipo,
    String titulo,
    String subtitulo,
    String contenido,
    String imagenUrl,
    String textoBoton,
    String enlaceBoton,
    Integer orden,
    Boolean visible) {
}
