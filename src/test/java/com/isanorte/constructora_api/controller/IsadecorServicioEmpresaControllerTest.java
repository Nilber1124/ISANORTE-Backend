package com.isanorte.constructora_api.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import com.isanorte.constructora_api.dto.response.IsadecorServicioEmpresaResponse;
import com.isanorte.constructora_api.service.IIsadecorServicioEmpresaService;

class IsadecorServicioEmpresaControllerTest {

    private final IIsadecorServicioEmpresaService service = mock(IIsadecorServicioEmpresaService.class);
    private final IsadecorServicioEmpresaController controller = new IsadecorServicioEmpresaController(service);

    @Test
    void findAllDelegaEnElServicio() {
        when(service.findAll()).thenReturn(List.of());

        var response = controller.findAll();

        assertSame(HttpStatus.OK, response.getStatusCode());
        assertEquals(0, response.getBody().size());
    }

    @Test
    void findByIdDelegaEnElServicio() {
        UUID id = UUID.randomUUID();
        IsadecorServicioEmpresaResponse esperado = null;
        when(service.findById(id)).thenReturn(esperado);

        var response = controller.findById(id);

        assertSame(HttpStatus.OK, response.getStatusCode());
        verify(service).findById(id);
    }

    @Test
    void deleteRetornaNoContent() {
        UUID id = UUID.randomUUID();

        var response = controller.delete(id);

        assertSame(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(service).delete(id);
    }
}
