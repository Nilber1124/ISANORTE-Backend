package com.isanorte.constructora_api.dto.request;

import java.util.List;
import java.util.UUID;

import com.isanorte.constructora_api.enums.TipoSeccionLanding;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ConfiguracionSitioRequest(
    @NotNull UUID empresaId,
    @Pattern(regexp = "[a-z0-9]+(?:-[a-z0-9]+)*") @Size(max = 100) String clave,
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
        @Size(max = 180) String etiqueta,
        String titulo,
        String subtitulo,
        String contenido,
        String imagenUrl,
        @Size(max = 300) String imagenAlt,
        String textoBoton,
        String enlaceBoton,
        Integer orden,
        Boolean visible) {
    }
}
