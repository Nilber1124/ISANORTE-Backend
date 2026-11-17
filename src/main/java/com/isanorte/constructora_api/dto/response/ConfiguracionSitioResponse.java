package com.isanorte.constructora_api.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.isanorte.constructora_api.enums.TipoSeccionLanding;

public record ConfiguracionSitioResponse(
    UUID id,
    String clave,
    String tituloSitio,
    String descripcionSitio,
    String logoUrl,
    String logoBlancoUrl,
    String faviconUrl,
    String colorPrimario,
    String colorSecundario,
    String textoPiePagina,
    EmpresaResumen empresa,
    List<SeccionResumen> secciones,
    LocalDateTime fechaActualizacion) {

    public record EmpresaResumen(UUID id, String nombreComercial) {
    }

    public record SeccionResumen(
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
        Boolean visible) {
    }
}
