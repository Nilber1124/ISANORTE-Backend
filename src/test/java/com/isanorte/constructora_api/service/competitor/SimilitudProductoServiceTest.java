package com.isanorte.constructora_api.service.competitor;

import static org.junit.jupiter.api.Assertions.*;
import java.math.BigDecimal;
import java.util.Map;
import org.junit.jupiter.api.Test;

class SimilitudProductoServiceTest {
    private final SimilitudProductoService service = new SimilitudProductoService();

    @Test
    void reconoceNombreCategoriaYCaracteristicasSimilares() {
        var reference = new ProductoReferencia("Piso SPC Roble Grey 6mm", "Pisos SPC",
                new BigDecimal("85"), "PEN", "m2",
                Map.of("Espesor", "6 mm", "Capa de uso", "0.5 mm"));
        var candidate = new ProductoCompetidor("PISOPAK", "Piso SPC Premium Roble Grey 6 mm",
                "https://www.pisopak.com/producto/roble", null, null, null, null, null,
                Map.of("Grosor", "6 mm", "Capa de desgaste", "0.5 mm"), BigDecimal.ZERO);

        assertTrue(service.calcular(reference, candidate)
                .compareTo(SimilitudProductoService.UMBRAL) >= 0);
    }

    @Test
    void rechazaUnProductoSinCoincidenciasObjetivas() {
        var reference = new ProductoReferencia("Piso SPC Roble Grey", "Pisos", null, "PEN", null,
                Map.of("Material", "SPC"));
        var candidate = new ProductoCompetidor("DECORPLAS", "Panel acústico nogal", "https://decorplas.pe/panel",
                null, null, null, null, null, Map.of("Material", "MDF"), BigDecimal.ZERO);

        assertTrue(service.calcular(reference, candidate)
                .compareTo(SimilitudProductoService.UMBRAL) < 0);
    }
}
