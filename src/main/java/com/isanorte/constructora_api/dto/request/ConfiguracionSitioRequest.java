package com.isanorte.constructora_api.dto.request;

import java.util.List;
import java.util.UUID;

import com.isanorte.constructora_api.enums.TipoSeccionLanding;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record ConfiguracionSitioRequest(
    @NotNull UUID empresaId,
    String tituloSitio,
    String descripcionSitio,
    String logoUrl,
    String logoBlancoUrl,
    String faviconUrl,
    String colorPrimario,
    String colorSecundario,
    String textoPiePagina,
    List<@Valid SeccionRequest> secciones) {

    public record SeccionRequest(
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
}
