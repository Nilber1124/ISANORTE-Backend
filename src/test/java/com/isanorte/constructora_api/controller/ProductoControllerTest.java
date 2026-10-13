package com.isanorte.constructora_api.controller;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import com.isanorte.constructora_api.dto.request.DestacadoProductoRequest;
import com.isanorte.constructora_api.dto.response.ProductoResponse;
import com.isanorte.constructora_api.service.IProductoService;

class ProductoControllerTest {

    @Test
    void updateDestacadoDelegaEnServicioYRetornaOk() {
        IProductoService service = mock(IProductoService.class);
        UUID id = UUID.randomUUID();
        DestacadoProductoRequest request = new DestacadoProductoRequest(true, 2);
        ProductoResponse expected = null;
        when(service.updateDestacado(id, request)).thenReturn(expected);
        ProductoController controller = new ProductoController(service);

        var response = controller.updateDestacado(id, request);

        assertSame(HttpStatus.OK, response.getStatusCode());
        assertSame(expected, response.getBody());
        verify(service).updateDestacado(id, request);
    }
}
