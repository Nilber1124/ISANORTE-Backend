package com.isanorte.constructora_api.dto.response;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import com.isanorte.constructora_api.enums.EstadoComparacionCompetidor;

public record ComparacionCompetidoresResponse(
        ProductoComparable productoIsadecor,
        List<ResultadoCompetidor> competidores,
        List<String> diferenciasEncontradas,
        OffsetDateTime fechaConsulta) {

    public record Caracteristica(String nombre, String valor) {}

    public record ProductoComparable(
            String nombre, BigDecimal precio, BigDecimal precioAnterior, String moneda,
            String unidadPrecio, List<Caracteristica> caracteristicas) {}

    public record ResultadoCompetidor(
            String empresa, EstadoComparacionCompetidor estado, boolean encontrado,
            String nombreProducto, String urlProducto, BigDecimal precio, BigDecimal precioAnterior,
            String moneda, String unidadPrecio, BigDecimal cantidadPorPresentacion,
            BigDecimal similitud, List<Caracteristica> caracteristicas,
            boolean comparablePrecio, BigDecimal diferenciaPrecio, String mensaje) {}
}
