package com.isanorte.constructora_api.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

class ConfiguracionCalculoTest {

    @Test
    void calculaCantidadExactaYDevuelveElDesglose() {
        var configuracion = configuracion(true, new BigDecimal("2.40"));

        var resultado = configuracion.calcular(new BigDecimal("4.80"));

        assertEquals(new BigDecimal("4.80"), resultado.getMedidaIngresada());
        assertEquals("m2", resultado.getUnidadEntrada());
        assertEquals(2, resultado.getCantidad());
        assertEquals("caja", resultado.getUnidadVenta());
    }

    @Test
    void redondeaHaciaArribaCuandoLaCoberturaNoEsExacta() {
        var configuracion = configuracion(true, new BigDecimal("2.40"));

        var resultado = configuracion.calcular(new BigDecimal("4.81"));

        assertEquals(3, resultado.getCantidad());
    }

    @Test
    void rechazaMedidaNulaCeroONegativa() {
        var configuracion = configuracion(true, BigDecimal.ONE);

        assertThrows(IllegalArgumentException.class, () -> configuracion.calcular(null));
        assertThrows(IllegalArgumentException.class, () -> configuracion.calcular(BigDecimal.ZERO));
        assertThrows(IllegalArgumentException.class,
                () -> configuracion.calcular(new BigDecimal("-0.01")));
    }

    @Test
    void rechazaCoberturaNulaCeroONegativa() {
        assertThrows(IllegalStateException.class,
                () -> configuracion(true, null).calcular(BigDecimal.ONE));
        assertThrows(IllegalStateException.class,
                () -> configuracion(true, BigDecimal.ZERO).calcular(BigDecimal.ONE));
        assertThrows(IllegalStateException.class,
                () -> configuracion(true, new BigDecimal("-1")).calcular(BigDecimal.ONE));
    }

    @Test
    void rechazaCalculadoraDeshabilitadaONoConfiguradaComoHabilitada() {
        assertThrows(IllegalStateException.class,
                () -> configuracion(false, BigDecimal.ONE).calcular(BigDecimal.ONE));
        assertThrows(IllegalStateException.class,
                () -> configuracion(null, BigDecimal.ONE).calcular(BigDecimal.ONE));
    }

    @Test
    void rechazaCantidadQueDesbordaElTipoEntero() {
        var configuracion = configuracion(true, BigDecimal.ONE);

        assertThrows(ArithmeticException.class,
                () -> configuracion.calcular(new BigDecimal("2147483648")));
    }

    private static ConfiguracionCalculo configuracion(Boolean habilitada, BigDecimal cobertura) {
        return ConfiguracionCalculo.builder()
                .habilitada(habilitada)
                .etiquetaEntrada("Área")
                .unidadEntrada("m2")
                .coberturaPorUnidad(cobertura)
                .unidadVenta("caja")
                .build();
    }
}