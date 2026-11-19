package com.isanorte.constructora_api.controller;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import com.isanorte.constructora_api.dto.request.ResenaHomeRequest;
import com.isanorte.constructora_api.service.IResenaAdminService;

class ResenaAdminControllerTest {

    private final IResenaAdminService service = mock(IResenaAdminService.class);
    private final ResenaAdminController controller = new ResenaAdminController(service);

    @Test
    void findAllDelegaYDevuelveOk() {
        when(service.findAll()).thenReturn(List.of());

        assertSame(HttpStatus.OK, controller.findAll().getStatusCode());
        verify(service).findAll();
    }

    @Test
    void updateHomeDelega() {
        UUID id = UUID.randomUUID();
        var request = new ResenaHomeRequest(true, 1);

        assertSame(HttpStatus.OK, controller.updateHome(id, request).getStatusCode());
        verify(service).updateHome(id, request);
    }
}
