package com.isanorte.constructora_api.dto.request;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.isanorte.constructora_api.enums.EstadoDisponibilidad;
import com.isanorte.constructora_api.enums.EstadoPublicacion;
import com.isanorte.constructora_api.enums.TipoDocumentoProducto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

public record ProductoRequest(
    @NotBlank String sku,
    @NotBlank String nombre,
    @NotBlank String slug,
    String resumen,
    @NotBlank String descripcion,
    @PositiveOrZero BigDecimal precioBase,
    @PositiveOrZero BigDecimal precioAnterior,
    @DecimalMin("0.0") @DecimalMax("100.0") BigDecimal descuentoPorcentaje,
    @NotNull EstadoDisponibilidad disponibilidad,
    Boolean destacado,
    Boolean retiroEnTienda,
    @NotNull EstadoPublicacion estado,
    String tituloSeo,
    String descripcionSeo,
    @NotNull UUID unidadNegocioId,
    Set<@NotNull UUID> categoriaIds,
    List<@Valid VarianteRequest> variantes,
    List<@Valid ImagenRequest> imagenes,
    List<@Valid EspecificacionRequest> especificaciones,
    List<@Valid DocumentoRequest> documentos,
    @Valid ConfiguracionCalculoRequest configuracionCalculo) {

    public record VarianteRequest(
        @NotBlank String sku,
        @NotBlank String nombre,
        String descripcion,
        @PositiveOrZero BigDecimal precio,
        Boolean disponible,
        String imagenUrl,
        Integer orden) {
    }

    public record ImagenRequest(
        @NotBlank String url,
        String altText,
        Boolean esPrincipal,
        Integer orden) {
    }

    public record EspecificacionRequest(
        @NotBlank String clave,
        @NotBlank String valor,
        String grupo,
        Integer orden) {
    }

    public record DocumentoRequest(
        @NotBlank String titulo,
        @NotBlank String url,
        @NotNull TipoDocumentoProducto tipo,
        String formato,
        @PositiveOrZero Long tamanoBytes) {
    }

    public record ConfiguracionCalculoRequest(
        Boolean habilitada,
        String etiquetaEntrada,
        String unidadEntrada,
        @NotNull @Positive BigDecimal coberturaPorUnidad,
        String unidadVenta,
        String textoAyuda) {
    }
}
