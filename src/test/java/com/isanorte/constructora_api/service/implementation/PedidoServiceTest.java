package com.isanorte.constructora_api.service.implementation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import com.isanorte.constructora_api.dto.request.EstadoPedidoRequest;
import com.isanorte.constructora_api.dto.request.PublicPedidoRequest;
import com.isanorte.constructora_api.dto.response.PedidoResponse;
import com.isanorte.constructora_api.dto.response.PublicPedidoResponse;
import com.isanorte.constructora_api.enums.EstadoPedido;
import com.isanorte.constructora_api.mapper.PedidoMapper;
import com.isanorte.constructora_api.model.DetallePedido;
import com.isanorte.constructora_api.model.Pedido;
import com.isanorte.constructora_api.model.Producto;
import com.isanorte.constructora_api.repository.ClienteRepository;
import com.isanorte.constructora_api.repository.ConfiguracionSitioRepository;
import com.isanorte.constructora_api.repository.DireccionClienteRepository;
import com.isanorte.constructora_api.repository.PedidoRepository;
import com.isanorte.constructora_api.repository.ProductoRepository;
import com.isanorte.constructora_api.repository.UnidadNegocioRepository;

class PedidoServiceTest {

    @Test
    void updateEstadoDebeSerIdempotenteSiElEstadoEsElMismo() {
        PedidoRepository pedidoRepository = mock(PedidoRepository.class);
        PedidoMapper mapper = mock(PedidoMapper.class);
        UUID id = UUID.randomUUID();
        Pedido pedido = Pedido.builder().id(id).estado(EstadoPedido.PENDIENTE_PAGO).build();
        when(pedidoRepository.findById(id)).thenReturn(Optional.of(pedido));
        PedidoResponse expected = dummyResponse();
        when(mapper.toResponse(pedido)).thenReturn(expected);
        PedidoService service = service(pedidoRepository, mapper, mock(ProductoRepository.class));

        PedidoResponse result = service.updateEstado(id, new EstadoPedidoRequest(EstadoPedido.PENDIENTE_PAGO));

        assertEquals(expected, result);
        verify(pedidoRepository, never()).saveAndFlush(any());
    }

    @Test
    void updateEstadoDebeRechazarTransicionDesdeCancelado() {
        PedidoRepository pedidoRepository = mock(PedidoRepository.class);
        PedidoMapper mapper = mock(PedidoMapper.class);
        UUID id = UUID.randomUUID();
        Pedido pedido = Pedido.builder().id(id).estado(EstadoPedido.CANCELADO).build();
        when(pedidoRepository.findById(id)).thenReturn(Optional.of(pedido));
        PedidoService service = service(pedidoRepository, mapper, mock(ProductoRepository.class));

        assertThrows(IllegalStateException.class,
                () -> service.updateEstado(id, new EstadoPedidoRequest(EstadoPedido.EN_PREPARACION)));
        verify(pedidoRepository, never()).saveAndFlush(any());
    }

    @Test
    void updateEstadoDebeRevertirStockAlCancelarUnPedidoConStockDescontado() {
        PedidoRepository pedidoRepository = mock(PedidoRepository.class);
        PedidoMapper mapper = mock(PedidoMapper.class);
        UUID id = UUID.randomUUID();
        Producto producto = Producto.builder().id(UUID.randomUUID()).stockActual(3).build();
        Pedido pedido = Pedido.builder().id(id).estado(EstadoPedido.PENDIENTE_PAGO).stockDescontado(true).build();
        DetallePedido detalle = DetallePedido.builder().producto(producto).cantidad(2).build();
        pedido.addDetalle(detalle);
        when(pedidoRepository.findById(id)).thenReturn(Optional.of(pedido));
        when(pedidoRepository.saveAndFlush(pedido)).thenReturn(pedido);
        PedidoResponse expected = dummyResponse();
        when(mapper.toResponse(pedido)).thenReturn(expected);
        PedidoService service = service(pedidoRepository, mapper, mock(ProductoRepository.class));

        PedidoResponse result = service.updateEstado(id, new EstadoPedidoRequest(EstadoPedido.CANCELADO));

        assertEquals(expected, result);
        assertEquals(5, producto.getStockActual());
        assertEquals(EstadoPedido.CANCELADO, pedido.getEstado());
        assertEquals(Boolean.FALSE, pedido.getStockDescontado());
        assertEquals(1, pedido.getSeguimientos().size());
        verify(pedidoRepository).saveAndFlush(pedido);
    }

    @Test
    void createPublicDebeReintentarYTenerExitoTrasUnChoqueDeConcurrencia() {
        PedidoRepository pedidoRepository = mock(PedidoRepository.class);
        PedidoMapper mapper = mock(PedidoMapper.class);
        @SuppressWarnings("unchecked")
        ObjectProvider<PedidoService> selfProvider = mock(ObjectProvider.class);
        PedidoService proxied = mock(PedidoService.class);
        when(selfProvider.getObject()).thenReturn(proxied);
        PublicPedidoResponse expected = dummyPublicResponse();
        when(proxied.crearPedidoTransaccional(any(), any(), any(), any()))
                .thenThrow(new ObjectOptimisticLockingFailureException(Producto.class, "id"))
                .thenReturn(expected);
        PedidoService service = service(pedidoRepository, mapper, mock(ProductoRepository.class), selfProvider);

        PublicPedidoResponse result = service.createPublic("site", "unit",
                new PublicPedidoRequest(null, null, "calle", "distrito", "ciudad", null, "999999999",
                        null, null, List.of()),
                UUID.randomUUID());

        assertEquals(expected, result);
    }

    @Test
    void createPublicDebeFallarClaramenteTrasAgotarLosReintentos() {
        PedidoRepository pedidoRepository = mock(PedidoRepository.class);
        PedidoMapper mapper = mock(PedidoMapper.class);
        @SuppressWarnings("unchecked")
        ObjectProvider<PedidoService> selfProvider = mock(ObjectProvider.class);
        PedidoService proxied = mock(PedidoService.class);
        when(selfProvider.getObject()).thenReturn(proxied);
        when(proxied.crearPedidoTransaccional(any(), any(), any(), any()))
                .thenThrow(new ObjectOptimisticLockingFailureException(Producto.class, "id"));
        PedidoService service = service(pedidoRepository, mapper, mock(ProductoRepository.class), selfProvider);

        PublicPedidoRequest request = new PublicPedidoRequest(null, null, "calle", "distrito", "ciudad", null,
                "999999999", null, null, List.of());

        assertThrows(IllegalStateException.class,
                () -> service.createPublic("site", "unit", request, UUID.randomUUID()));
    }

    private PedidoResponse dummyResponse() {
        return new PedidoResponse(UUID.randomUUID(), "PED-TEST", UUID.randomUUID(), null, "calle", "distrito",
                "ciudad", null, "999999999", null, null, EstadoPedido.PENDIENTE_PAGO, null, false,
                List.of(), List.of(), null, null);
    }

    private PublicPedidoResponse dummyPublicResponse() {
        return new PublicPedidoResponse("PED-TEST", "calle", "distrito", "ciudad",
                EstadoPedido.PENDIENTE_PAGO, null, List.of(), null);
    }

    private PedidoService service(PedidoRepository pedidoRepository, PedidoMapper mapper,
            ProductoRepository productoRepository) {
        return service(pedidoRepository, mapper, productoRepository, mock(ObjectProvider.class));
    }

    private PedidoService service(PedidoRepository pedidoRepository, PedidoMapper mapper,
            ProductoRepository productoRepository, ObjectProvider<PedidoService> self) {
        return new PedidoService(pedidoRepository, productoRepository, mock(ConfiguracionSitioRepository.class),
                mock(UnidadNegocioRepository.class), mock(ClienteRepository.class),
                mock(DireccionClienteRepository.class), mapper, self);
    }
}
