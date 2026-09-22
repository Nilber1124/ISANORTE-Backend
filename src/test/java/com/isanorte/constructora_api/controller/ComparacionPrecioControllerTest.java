package com.isanorte.constructora_api.controller;

import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import com.isanorte.constructora_api.dto.response.ComparacionPrecioResponse;
import com.isanorte.constructora_api.exception.GlobalExceptionHandler;
import com.isanorte.constructora_api.service.*;
import java.time.OffsetDateTime;
import static com.isanorte.constructora_api.enums.EstadoComparacionPrecio.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ComparacionPrecioControllerTest {
    @Test void endpointPublicoDevuelveContratoYNoCachea() throws Exception {
        var service = mock(IComparacionPrecioService.class);
        when(service.comparar(eq("isanorte"), eq("isadecor"), eq("mesa"), any()))
                .thenReturn(new ComparacionPrecioResponse("Mesa", null, null, null, null, null,
                        "PEN", null, null, null, false, PRICE_NOT_FOUND, "Sin precio", OffsetDateTime.now()));
        var mvc = MockMvcBuilders.standaloneSetup(new PublicContentController(
                mock(IPublicContentService.class), mock(ISolicitudContactoService.class), service))
                .setControllerAdvice(new GlobalExceptionHandler()).build();
        mvc.perform(post("/api/publico/sitios/isanorte/unidades/isadecor/productos/mesa/comparar-precio")
                .contentType("application/json").content("{\"urlExterna\":\"https://tienda.example/mesa\"}"))
                .andExpect(status().isOk()).andExpect(header().string("Cache-Control", "no-store"))
                .andExpect(jsonPath("$.estado").value("PRICE_NOT_FOUND"))
                .andExpect(jsonPath("$.comparable").value(false));
    }
}

