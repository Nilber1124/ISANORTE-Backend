package com.isanorte.constructora_api.repository.specification;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

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

        verify(criteria).equal(modalidad, ModalidadVenta.COTIZAR);
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
}
