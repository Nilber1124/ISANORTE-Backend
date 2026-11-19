package com.isanorte.constructora_api.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.isanorte.constructora_api.enums.EstadoDisponibilidad;
import com.isanorte.constructora_api.enums.EstadoStock;
import com.isanorte.constructora_api.model.Producto;
import com.isanorte.constructora_api.model.VarianteProducto;

class DisponibilidadStockTest {

    private VarianteProducto variante(Integer stock) {
        return VarianteProducto.builder().stockActual(stock).build();
    }

    private Producto productoConVariantes(Integer stockMinimo, VarianteProducto... variantes) {
        return Producto.builder().stockMinimo(stockMinimo)
                .disponibilidad(EstadoDisponibilidad.DISPONIBLE).variantes(new ArrayList<>(List.of(variantes))).build();
    }

    @Test
    void estadoSinStockEsSinControl() {
        assertEquals(EstadoStock.SIN_CONTROL, DisponibilidadStock.estado(null, 5));
    }

    @Test
    void estadoConStockCeroONegativoEsAgotado() {
        assertEquals(EstadoStock.AGOTADO, DisponibilidadStock.estado(0, 5));
        assertEquals(EstadoStock.AGOTADO, DisponibilidadStock.estado(-1, null));
    }

    @Test
    void estadoBajoOEnElUmbralEsQuedan() {
        assertEquals(EstadoStock.QUEDAN, DisponibilidadStock.estado(5, 5));
        assertEquals(EstadoStock.QUEDAN, DisponibilidadStock.estado(1, 5));
    }

    @Test
    void estadoSobreElUmbralOSinUmbralEsDisponible() {
        assertEquals(EstadoStock.DISPONIBLE, DisponibilidadStock.estado(6, 5));
        assertEquals(EstadoStock.DISPONIBLE, DisponibilidadStock.estado(1, null));
    }

    @Test
    void resumenSumaLasVariantesCuandoTodasControlanStock() {
        Producto producto = productoConVariantes(10, variante(4), variante(3));

        var resumen = DisponibilidadStock.resumen(producto);

        assertEquals(7, resumen.quedan());
        assertEquals(EstadoStock.QUEDAN, resumen.estado());
    }

    @Test
    void resumenEsSinControlSiAlgunaVarianteNoControlaStock() {
        Producto producto = productoConVariantes(10, variante(4), variante(null));

        var resumen = DisponibilidadStock.resumen(producto);

        assertNull(resumen.quedan());
        assertEquals(EstadoStock.SIN_CONTROL, resumen.estado());
    }

    @Test
    void resumenUsaElStockDelProductoSinVariantes() {
        Producto producto = Producto.builder().stockActual(0).build();

        assertEquals(EstadoStock.AGOTADO, DisponibilidadStock.resumen(producto).estado());
    }

    @Test
    void recalcularMarcaAgotadoSiTodasLasVariantesEstanEnCero() {
        Producto producto = productoConVariantes(null, variante(0), variante(0));

        DisponibilidadStock.recalcular(producto);

        assertEquals(EstadoDisponibilidad.AGOTADO, producto.getDisponibilidad());
    }

    @Test
    void recalcularReactivaUnProductoAgotadoCuandoHayStock() {
        Producto producto = productoConVariantes(null, variante(0), variante(5));
        producto.setDisponibilidad(EstadoDisponibilidad.AGOTADO);

        DisponibilidadStock.recalcular(producto);

        assertEquals(EstadoDisponibilidad.DISPONIBLE, producto.getDisponibilidad());
    }

    @Test
    void recalcularMarcaAgotadoUnProductoSinVariantesSinStock() {
        Producto producto = Producto.builder().stockActual(0).disponibilidad(EstadoDisponibilidad.DISPONIBLE).build();

        DisponibilidadStock.recalcular(producto);

        assertEquals(EstadoDisponibilidad.AGOTADO, producto.getDisponibilidad());
    }

    @Test
    void recalcularNoTocaUnProductoSinControlDeStock() {
        Producto producto = Producto.builder().disponibilidad(EstadoDisponibilidad.CONSULTAR).build();

        DisponibilidadStock.recalcular(producto);

        assertEquals(EstadoDisponibilidad.CONSULTAR, producto.getDisponibilidad());
    }
}
