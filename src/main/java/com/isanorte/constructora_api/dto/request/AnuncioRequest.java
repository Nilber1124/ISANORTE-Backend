package com.isanorte.constructora_api.dto.request;

import java.time.LocalDateTime;

import com.isanorte.constructora_api.enums.DestinoAnuncio;
import com.isanorte.constructora_api.enums.TipoAccionAnuncio;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record AnuncioRequest(
        @NotBlank @Size(max = 160) String titulo,
        @NotBlank @Size(max = 320) String descripcionResumida,
        @NotBlank String contenidoDetallado,
        String condiciones,
        @NotBlank @Size(max = 500) String imagenUrl,
        @NotBlank @Size(max = 60) String etiqueta,
        @NotNull DestinoAnuncio destino,
        @NotNull TipoAccionAnuncio tipoAccion,
        @NotBlank @Size(max = 500) String destinoAccion,
        @NotBlank @Size(max = 80) String textoBoton,
        LocalDateTime fechaInicio,
        LocalDateTime fechaFin,
        @NotNull Boolean activo,
        @NotNull @PositiveOrZero Integer orden) {

    @AssertTrue(message = "la fecha final debe ser igual o posterior a la fecha inicial")
    public boolean isVigenciaValida() {
        return fechaInicio == null || fechaFin == null || !fechaFin.isBefore(fechaInicio);
    }
}

