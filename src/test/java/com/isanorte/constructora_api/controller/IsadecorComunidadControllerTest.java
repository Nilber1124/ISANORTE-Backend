package com.isanorte.constructora_api.controller;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import com.isanorte.constructora_api.service.IIsadecorComunidadService;

class IsadecorComunidadControllerTest {

    private final IIsadecorComunidadService service = mock(IIsadecorComunidadService.class);
    private final IsadecorComunidadController controller = new IsadecorComunidadController(service);

    @Test
    void findPublicacionesDelegaYDevuelveOk() {
        when(service.findPublicaciones("isanorte", "isadecor")).thenReturn(List.of());

        assertSame(HttpStatus.OK, controller.findPublicaciones("isanorte", "isadecor").getStatusCode());
        verify(service).findPublicaciones("isanorte", "isadecor");
    }
}
