package com.isanorte.constructora_api.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.isanorte.constructora_api.enums.EstadoDisponibilidad;
import com.isanorte.constructora_api.enums.EstadoPublicacion;

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
    List<VarianteProductoResponse> variantes,
    List<ImagenProductoResponse> imagenes,
    List<EspecificacionProductoResponse> especificaciones,
    List<DocumentoProductoResponse> documentos,
    ConfiguracionCalculoResponse configuracionCalculo,
    LocalDateTime fechaCreacion,
    LocalDateTime fechaActualizacion) {

    public record UnidadNegocioResumen(UUID id, String nombre, String slug) {
    }

    public record CategoriaResumen(UUID id, String nombre, String slug) {
    }

}
