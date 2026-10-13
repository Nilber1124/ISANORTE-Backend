package com.isanorte.constructora_api.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

import com.isanorte.constructora_api.enums.DestinoAnuncio;
import com.isanorte.constructora_api.enums.TipoAccionAnuncio;
import com.isanorte.constructora_api.enums.UbicacionAnuncio;

public record AnuncioResponse(
        UUID id,
        String titulo,
        String descripcionResumida,
        String contenidoDetallado,
        String condiciones,
        String imagenUrl,
        String imagenHoverUrl,
        String etiqueta,
        DestinoAnuncio destino,
        UbicacionAnuncio ubicacion,
        TipoAccionAnuncio tipoAccion,
        String destinoAccion,
        String textoBoton,
        LocalDateTime fechaInicio,
        LocalDateTime fechaFin,
        Boolean activo,
        Integer orden,
        LocalDateTime fechaCreacion,
        LocalDateTime fechaActualizacion) {
}

