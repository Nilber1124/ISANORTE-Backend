package com.isanorte.constructora_api.dto.request;

import java.util.UUID;

import com.isanorte.constructora_api.enums.TipoSeccionLanding;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record SeccionLandingRequest(
    @NotNull UUID configuracionSitioId,
    @NotNull TipoSeccionLanding tipo,
    @Size(max = 180) String etiqueta,
    String titulo,
    String subtitulo,
    String contenido,
    String imagenUrl,
    @Size(max = 300) String imagenAlt,
    String textoBoton,
    String enlaceBoton,
    @PositiveOrZero Integer orden,
    Boolean visible) {
}
