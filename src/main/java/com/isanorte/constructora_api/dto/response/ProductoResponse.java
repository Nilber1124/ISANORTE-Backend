package com.isanorte.constructora_api.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.isanorte.constructora_api.enums.EstadoDisponibilidad;
import com.isanorte.constructora_api.enums.EstadoPublicacion;
import com.isanorte.constructora_api.enums.TipoDocumentoProducto;

public record ProductoResponse(
    UUID id,
    String sku,
    String nombre,
    String slug,
    String resumen,
    String descripcion,
    BigDecimal precioBase,
    BigDecimal precioAnterior,
    BigDecimal descuentoPorcentaje,
    EstadoDisponibilidad disponibilidad,
    Boolean destacado,
    EstadoPublicacion estado,
    String tituloSeo,
    String descripcionSeo,
    UnidadNegocioResumen unidadNegocio,
    Set<CategoriaResumen> categorias,
    List<VarianteResponse> variantes,
    List<ImagenResponse> imagenes,
    List<EspecificacionResponse> especificaciones,
    List<DocumentoResponse> documentos,
    ConfiguracionCalculoResponse configuracionCalculo,
    LocalDateTime fechaCreacion,
    LocalDateTime fechaActualizacion) {

    public record UnidadNegocioResumen(UUID id, String nombre, String slug) {
    }

    public record CategoriaResumen(UUID id, String nombre, String slug) {
    }

    public record VarianteResponse(
        UUID id, String sku, String nombre, String descripcion, BigDecimal precio,
        Boolean disponible, String imagenUrl, Integer orden) {
    }

    public record ImagenResponse(UUID id, String url, String altText, Boolean esPrincipal, Integer orden) {
    }

    public record EspecificacionResponse(UUID id, String clave, String valor, String grupo, Integer orden) {
    }

    public record DocumentoResponse(
        UUID id, String titulo, String url, TipoDocumentoProducto tipo, String formato, Long tamanoBytes) {
    }

    public record ConfiguracionCalculoResponse(
        UUID id, Boolean habilitada, String etiquetaEntrada, String unidadEntrada,
        BigDecimal coberturaPorUnidad, String unidadVenta, String textoAyuda) {
    }
}
