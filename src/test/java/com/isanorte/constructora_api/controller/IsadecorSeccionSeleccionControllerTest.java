package com.isanorte.constructora_api.controller;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import com.isanorte.constructora_api.dto.request.SeccionProductosIdsRequest;
import com.isanorte.constructora_api.service.IIsadecorSeccionSeleccionService;

class IsadecorSeccionSeleccionControllerTest {

    private final IIsadecorSeccionSeleccionService service = mock(IIsadecorSeccionSeleccionService.class);
    private final IsadecorSeccionSeleccionController controller = new IsadecorSeccionSeleccionController(service);
    private final UUID seccionId = UUID.randomUUID();
    private final SeccionProductosIdsRequest request = new SeccionProductosIdsRequest(List.of(UUID.randomUUID()));

    @Test
    void fijadosYExcluidosDelegan() {
        when(service.fijados(seccionId)).thenReturn(List.of());
        when(service.excluidos(seccionId)).thenReturn(List.of());

        assertSame(HttpStatus.OK, controller.fijados(seccionId).getStatusCode());
        assertSame(HttpStatus.OK, controller.excluidos(seccionId).getStatusCode());
        verify(service).fijados(seccionId);
        verify(service).excluidos(seccionId);
    }

    @Test
    void reemplazarFijadosYExcluidosDelegan() {
        controller.reemplazarFijados(seccionId, request);
        controller.reemplazarExcluidos(seccionId, request);

        verify(service).reemplazarFijados(seccionId, request);
        verify(service).reemplazarExcluidos(seccionId, request);
    }
}
