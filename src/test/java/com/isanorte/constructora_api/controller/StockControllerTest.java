package com.isanorte.constructora_api.controller;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;

import com.isanorte.constructora_api.dto.request.AjusteStockRequest;
import com.isanorte.constructora_api.dto.response.StockResponse;
import com.isanorte.constructora_api.enums.EstadoStock;
import com.isanorte.constructora_api.enums.TipoMovimientoStock;
import com.isanorte.constructora_api.service.IStockService;

class StockControllerTest {

    private final IStockService service = mock(IStockService.class);
    private final StockController controller = new StockController(service);
    private final AjusteStockRequest request = new AjusteStockRequest(TipoMovimientoStock.ENTRADA, 3, "Reposición");

    @Test
    void ajustarProductoDelegaConElNombreDelUsuario() {
        UUID productoId = UUID.randomUUID();
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("admin");
        StockResponse esperado = new StockResponse(productoId, null, 3, 5, EstadoStock.QUEDAN, 3);
        when(service.ajustarProducto(productoId, request, "admin")).thenReturn(esperado);

        var response = controller.ajustarProducto(productoId, request, auth);

        assertSame(HttpStatus.OK, response.getStatusCode());
        assertSame(esperado, response.getBody());
    }

    @Test
    void ajustarProductoSinAutenticacionPasaUsuarioNulo() {
        UUID productoId = UUID.randomUUID();

        controller.ajustarProducto(productoId, request, null);

        verify(service).ajustarProducto(productoId, request, null);
    }

    @Test
    void ajustarVarianteDelegaEnElServicio() {
        UUID productoId = UUID.randomUUID();
        UUID varianteId = UUID.randomUUID();

        var response = controller.ajustarVariante(productoId, varianteId, request, null);

        assertSame(HttpStatus.OK, response.getStatusCode());
        verify(service).ajustarVariante(productoId, varianteId, request, null);
    }

    @Test
    void historialDelegaEnElServicio() {
        UUID productoId = UUID.randomUUID();
        when(service.historial(productoId)).thenReturn(List.of());

        var response = controller.historial(productoId);

        assertSame(HttpStatus.OK, response.getStatusCode());
        verify(service).historial(productoId);
    }
}
