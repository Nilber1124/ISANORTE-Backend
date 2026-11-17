package com.isanorte.constructora_api.service.implementation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

class PublicContentServiceOfertasTest {

    @Test
    void separaLasOfertasPorBarraOSaltoDeLinea() {
        assertEquals(List.of("Mobiliario a medida", "Cocinas y closets", "Diseño de interiores"),
                PublicContentService.splitOfertas("Mobiliario a medida | Cocinas y closets\nDiseño de interiores"));
    }

    @Test
    void ignoraElementosVaciosYValoresNulos() {
        assertEquals(List.of("A", "B"), PublicContentService.splitOfertas(" A || B | "));
        assertTrue(PublicContentService.splitOfertas(null).isEmpty());
        assertTrue(PublicContentService.splitOfertas("   ").isEmpty());
    }
}
