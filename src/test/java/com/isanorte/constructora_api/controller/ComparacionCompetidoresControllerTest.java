package com.isanorte.constructora_api.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import java.time.OffsetDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import com.isanorte.constructora_api.dto.response.ComparacionCompetidoresResponse;
import com.isanorte.constructora_api.dto.response.ComparacionCompetidoresResponse.ProductoComparable;
import com.isanorte.constructora_api.service.*;

class ComparacionCompetidoresControllerTest {
    @Test void exponeGetPublicoSinCache() throws Exception {
        var service = mock(IComparacionCompetidoresService.class);
        when(service.comparar("isanorte", "isadecor", "piso"))
                .thenReturn(new ComparacionCompetidoresResponse(
                        new ProductoComparable("Piso", null, null, "PEN", null, List.of()),
                        List.of(), List.of(), OffsetDateTime.now()));
        var controller = new PublicContentController(mock(IPublicContentService.class),
                mock(ISolicitudContactoService.class), mock(IComparacionPrecioService.class), service);
        var mvc = MockMvcBuilders.standaloneSetup(controller).build();

        mvc.perform(get("/api/publico/sitios/isanorte/unidades/isadecor/productos/piso/comparacion-competidores"))
                .andExpect(status().isOk()).andExpect(header().string("Cache-Control", "no-store"))
                .andExpect(jsonPath("$.productoIsadecor.nombre").value("Piso"));
    }
}
