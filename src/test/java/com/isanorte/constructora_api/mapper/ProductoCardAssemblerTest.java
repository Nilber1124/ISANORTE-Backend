package com.isanorte.constructora_api.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.isanorte.constructora_api.enums.EstadoStock;
import com.isanorte.constructora_api.model.ImagenProducto;
import com.isanorte.constructora_api.model.Producto;
import com.isanorte.constructora_api.model.VarianteProducto;
import com.isanorte.constructora_api.repository.projection.ResumenResena;

class ProductoCardAssemblerTest {
    @Test
    void cardIncluyeImagenPrincipalAcabadosValoracionYStock() {
        Producto p = producto("Panel");
        p.setStockMinimo(5);
        p.getImagenes().add(ImagenProducto.builder().url("secundaria.webp").esPrincipal(false).orden(0).build());
        p.getImagenes().add(ImagenProducto.builder().url("principal.webp").altText("alt").esPrincipal(true).orden(3).build());
        p.getVariantes().add(VarianteProducto.builder().nombre("Roble").colorHex("#caa").stockActual(3).orden(0).build());
        p.getVariantes().add(VarianteProducto.builder().nombre("Nogal").stockActual(0).orden(1).build());
        ResumenResena resumen = new ResumenResena() {
            @Override public UUID getProductoId() { return p.getId(); }
            @Override public Double getPromedio() { return 4.5; }
            @Override public Long getCantidad() { return 2L; }
        };

        var card = ProductoCardAssembler.card(p, Map.of(p.getId(), resumen));

        assertEquals("principal.webp", card.imagen().url());
        assertEquals(2, card.acabados().size());
        assertEquals(false, card.acabados().get(0).agotado());
        assertEquals(true, card.acabados().get(1).agotado());
        assertEquals(4.5, card.valoracion());
        assertEquals(2L, card.resenasCantidad());
        assertEquals(EstadoStock.QUEDAN, card.estadoStock());
        assertEquals(3, card.quedan());
    }

    @Test
    void cardRedondeaLaValoracionAUnDecimal() {
        Producto p = producto("Panel");
        ResumenResena resumen = new ResumenResena() {
            @Override public UUID getProductoId() { return p.getId(); }
            @Override public Double getPromedio() { return 4.3333333333; }
            @Override public Long getCantidad() { return 3L; }
        };

        assertEquals(4.3, ProductoCardAssembler.card(p, Map.of(p.getId(), resumen)).valoracion());
    }

    @Test
    void cardSinImagenNiResenasNoFalla() {
        var card = ProductoCardAssembler.card(producto("Solo"), Map.of());

        assertNull(card.imagen());
        assertNull(card.valoracion());
        assertEquals(0L, card.resenasCantidad());
        assertNull(card.quedan());
    }

    private Producto producto(String nombre) {
        return Producto.builder().id(UUID.randomUUID()).nombre(nombre).build();
    }
}
