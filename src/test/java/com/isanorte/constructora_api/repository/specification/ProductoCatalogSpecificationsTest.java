package com.isanorte.constructora_api.repository.specification;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.isanorte.constructora_api.enums.ModalidadVenta;
import com.isanorte.constructora_api.model.Producto;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

class ProductoCatalogSpecificationsTest {
    @SuppressWarnings("unchecked")
    private final Root<Producto> producto = mock(Root.class);
    private final CriteriaBuilder criteria = mock(CriteriaBuilder.class);

    @Test
    void omiteModalidadInstalacionYAcabadoSiNoSeIndican() {
        Predicate sinFiltro = mock(Predicate.class);
        when(criteria.conjunction()).thenReturn(sinFiltro);

        assertSame(sinFiltro, ProductoCatalogSpecifications.modalidad(null).toPredicate(producto, null, criteria));
        assertSame(sinFiltro, ProductoCatalogSpecifications.facilInstalacion(null).toPredicate(producto, null, criteria));
        assertSame(sinFiltro, ProductoCatalogSpecifications.acabado(" ").toPredicate(producto, null, criteria));
        verifyNoInteractions(producto);
    }

    @Test
    void filtraModalidadYFalseExplicito() {
        Path<Object> modalidad = mock();
        Path<Object> facil = mock();
        when(producto.get("modalidad")).thenReturn(modalidad);
        when(producto.get("facilInstalacion")).thenReturn(facil);

        ProductoCatalogSpecifications.modalidad(ModalidadVenta.COTIZAR).toPredicate(producto, null, criteria);
        ProductoCatalogSpecifications.facilInstalacion(false).toPredicate(producto, null, criteria);

        verify(modalidad).in(ModalidadVenta.COTIZAR, ModalidadVenta.AMBAS);
        verify(criteria).equal(facil, false);
    }

    @Test
    void aplicaUnRangoInclusivoConMinimoCero() {
        Path<Number> precio = mock();
        when(producto.<Number>get("precioBase")).thenReturn(precio);

        ProductoCatalogSpecifications.rangoPrecio(BigDecimal.ZERO, BigDecimal.TEN)
                .toPredicate(producto, null, criteria);

        verify(criteria).ge(precio, BigDecimal.ZERO);
        verify(criteria).le(precio, BigDecimal.TEN);
    }

    @Test
    void separaListasPorComaSinVaciosNiRepetidos() {
        assertEquals(List.of("pisos", "techos"), ProductoCatalogSpecifications.splitValues(" pisos, techos ,, pisos"));
        assertEquals(List.of(), ProductoCatalogSpecifications.splitValues(null));
        assertEquals(List.of(), ProductoCatalogSpecifications.splitValues(" , "));
    }

    @Test
    void limitaLaCantidadDeValoresDeUnaLista() {
        String muchos = String.join(",", java.util.stream.IntStream.range(0, 40).mapToObj(i -> "c" + i).toList());

        assertEquals(12, ProductoCatalogSpecifications.splitValues(muchos).size());
    }

    @Test
    void sinColeccionesNoRestringe() {
        Predicate sinFiltro = mock(Predicate.class);
        when(criteria.conjunction()).thenReturn(sinFiltro);

        assertSame(sinFiltro,
                ProductoCatalogSpecifications.categoriaSlug(" ", UUID.randomUUID()).toPredicate(producto, null, criteria));
    }

    @Test
    void normalizaTextoSinTildesNiMayusculas() {
        assertEquals("panel sandwich", ProductoCatalogSpecifications.normalizarTexto("  Panel SÁNDWICH "));
        assertEquals("diseno", ProductoCatalogSpecifications.normalizarTexto("Diseño"));
        assertEquals("", ProductoCatalogSpecifications.normalizarTexto(null));
    }

    @Test
    void separaLaBusquedaEnPalabrasEnSingular() {
        assertEquals(List.of("panel", "sandwich"), ProductoCatalogSpecifications.terminosDeBusqueda("Paneles SÁNDWICH"));
        assertEquals(List.of("piso", "autoadhesivo"), ProductoCatalogSpecifications.terminosDeBusqueda("pisos, autoadhesivos"));
        assertEquals(List.of("pvc"), ProductoCatalogSpecifications.terminosDeBusqueda("PVC pvc"));
        assertEquals(List.of(), ProductoCatalogSpecifications.terminosDeBusqueda("  ,, "));
    }

    @Test
    void limitaLaCantidadDePalabrasDeUnaBusqueda() {
        assertEquals(6, ProductoCatalogSpecifications.terminosDeBusqueda("a1 b2 c3 d4 e5 f6 g7 h8 i9").size());
    }

    @Test
    void ambasNoRestringePorModalidad() {
        Predicate sinFiltro = mock(Predicate.class);
        when(criteria.conjunction()).thenReturn(sinFiltro);

        assertSame(sinFiltro,
                ProductoCatalogSpecifications.modalidad(ModalidadVenta.AMBAS).toPredicate(producto, null, criteria));
    }
}
