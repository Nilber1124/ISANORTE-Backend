package com.isanorte.constructora_api.dto.request;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import com.isanorte.constructora_api.enums.OrdenCatalogoProducto;

class PublicProductCatalogQueryTest {
    @Test
    void aceptaLimitesIndependientesYElMismoPrecioEnAmbosExtremos() {
        assertDoesNotThrow(() -> query(null, BigDecimal.ZERO));
        assertDoesNotThrow(() -> query(BigDecimal.ZERO, null));
        assertDoesNotThrow(() -> query(new BigDecimal("10.00"), BigDecimal.TEN));
    }

    @Test
    void rechazaPreciosNegativosOInvertidos() {
        BigDecimal negativo = new BigDecimal("-0.01");
        assertThrows(IllegalArgumentException.class, () -> query(negativo, null));
        assertThrows(IllegalArgumentException.class, () -> query(null, negativo));
        assertThrows(IllegalArgumentException.class, () -> query(BigDecimal.TEN, BigDecimal.ONE));
    }

    private PublicProductCatalogQuery query(BigDecimal min, BigDecimal max) {
        return new PublicProductCatalogQuery(0, 12, null, null, false, false, false,
                OrdenCatalogoProducto.DESTACADOS, null, null, min, max, null);
    }
}
