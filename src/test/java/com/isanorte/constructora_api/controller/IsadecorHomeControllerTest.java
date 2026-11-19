package com.isanorte.constructora_api.controller;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import com.isanorte.constructora_api.dto.response.IsadecorHomeResponse;
import com.isanorte.constructora_api.service.IIsadecorHomeService;

class IsadecorHomeControllerTest {

    private final IIsadecorHomeService service = mock(IIsadecorHomeService.class);
    private final IsadecorHomeController controller = new IsadecorHomeController(service);

    @Test
    void findHomeDelegaYDevuelveOk() {
        var home = new IsadecorHomeResponse(null, List.of(), List.of(), null, null, null, null, List.of(), List.of());
        when(service.findHome("isanorte", "isadecor")).thenReturn(home);

        var response = controller.findHome("isanorte", "isadecor");

        assertSame(HttpStatus.OK, response.getStatusCode());
        assertSame(home, response.getBody());
        verify(service).findHome("isanorte", "isadecor");
    }
}
