package com.isanorte.constructora_api.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.isanorte.constructora_api.model.VarianteProducto;

class PublicProductMapperTest {
    private final PublicProductMapper mapper = new PublicProductMapper();

    @Test
    void publicaColorYStockRestanteCuandoEsBajo() {
        var variante = VarianteProducto.builder().nombre("Roble").colorHex("#AA8866").stockActual(3).build();

        var variantePublica = mapper.toVariant(variante, 3);

        assertEquals("#AA8866", variantePublica.colorHex());
        assertFalse(variantePublica.agotado());
        assertEquals(3, variantePublica.quedan());
        assertNull(mapper.toVariant(variante, 2).quedan());

        variante.setStockActual(null);
        assertNull(mapper.toVariant(variante, 3).quedan());
    }

    @Test
    void distingueUnaVarianteAgotadaODesactivada() {
        var variante = VarianteProducto.builder().nombre("Nogal").stockActual(0).build();
        assertTrue(mapper.toVariant(variante, 3).agotado());

        variante.setStockActual(2);
        variante.setDisponible(false);
        var variantePublica = mapper.toVariant(variante, 3);
        assertTrue(variantePublica.agotado());
        assertNull(variantePublica.quedan());
    }
}
