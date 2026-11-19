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
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import com.isanorte.constructora_api.dto.request.EstadoPedidoRequest;
import com.isanorte.constructora_api.dto.request.PublicPedidoRequest;
import com.isanorte.constructora_api.dto.response.PedidoResponse;
import com.isanorte.constructora_api.dto.response.PublicPedidoResponse;
import com.isanorte.constructora_api.enums.EstadoDisponibilidad;
import com.isanorte.constructora_api.enums.TipoMovimientoStock;
import com.isanorte.constructora_api.enums.EstadoPedido;
import com.isanorte.constructora_api.mapper.PedidoMapper;
import com.isanorte.constructora_api.model.DetallePedido;
import com.isanorte.constructora_api.model.Pedido;
import com.isanorte.constructora_api.model.VarianteProducto;
import com.isanorte.constructora_api.model.MovimientoStock;
import com.isanorte.constructora_api.model.Producto;
import com.isanorte.constructora_api.repository.ClienteRepository;
import com.isanorte.constructora_api.repository.ConfiguracionSitioRepository;
import com.isanorte.constructora_api.repository.DireccionClienteRepository;
import com.isanorte.constructora_api.repository.MovimientoStockRepository;
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
        EstadoPedidoRequest request = new EstadoPedidoRequest(EstadoPedido.EN_PREPARACION);

        assertThrows(IllegalStateException.class, () -> service.updateEstado(id, request));
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
        UUID clienteId = UUID.randomUUID();

        assertThrows(IllegalStateException.class, () -> service.createPublic("site", "unit", request, clienteId));
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
                mock(DireccionClienteRepository.class), mapper, self, mock(MovimientoStockRepository.class));
    }

    // ---- Stock por acabado e historial ----

    private PedidoService serviceConMovimientos(ProductoRepository productoRepository, MovimientoStockRepository movimientos) {
        return new PedidoService(mock(PedidoRepository.class), productoRepository, mock(ConfiguracionSitioRepository.class),
                mock(UnidadNegocioRepository.class), mock(ClienteRepository.class),
                mock(DireccionClienteRepository.class), mock(PedidoMapper.class), mock(ObjectProvider.class), movimientos);
    }

    private Producto productoConVariante(Integer stockProducto, VarianteProducto variante) {
        return Producto.builder().id(UUID.randomUUID()).nombre("Panel").stockActual(stockProducto)
                .disponibilidad(EstadoDisponibilidad.DISPONIBLE)
                .variantes(new java.util.ArrayList<>(List.of(variante))).build();
    }

    @Test
    void descontarStockUsaElStockDelAcabadoCuandoLoControla() {
        VarianteProducto variante = VarianteProducto.builder().id(UUID.randomUUID()).nombre("Nogal").stockActual(5).disponible(true).build();
        Producto producto = productoConVariante(null, variante);
        ProductoRepository productoRepository = mock(ProductoRepository.class);

        serviceConMovimientos(productoRepository, mock(MovimientoStockRepository.class)).descontarStock(producto, variante, 2);

        assertEquals(3, variante.getStockActual());
        assertEquals(Boolean.TRUE, variante.getDisponible());
        verify(productoRepository).saveAndFlush(producto);
    }

    @Test
    void descontarStockRechazaCuandoElAcabadoNoAlcanza() {
        VarianteProducto variante = VarianteProducto.builder().id(UUID.randomUUID()).nombre("Nogal").stockActual(1).build();
        Producto producto = productoConVariante(null, variante);
        PedidoService service = serviceConMovimientos(mock(ProductoRepository.class), mock(MovimientoStockRepository.class));

        assertThrows(IllegalStateException.class, () -> service.descontarStock(producto, variante, 2));
        assertEquals(1, variante.getStockActual());
    }

    @Test
    void descontarLaUltimaUnidadMarcaElAcabadoYElProductoComoAgotados() {
        VarianteProducto variante = VarianteProducto.builder().id(UUID.randomUUID()).nombre("Nogal").stockActual(2).disponible(true).build();
        Producto producto = productoConVariante(null, variante);

        serviceConMovimientos(mock(ProductoRepository.class), mock(MovimientoStockRepository.class)).descontarStock(producto, variante, 2);

        assertEquals(Boolean.FALSE, variante.getDisponible());
        assertEquals(EstadoDisponibilidad.AGOTADO, producto.getDisponibilidad());
    }

    @Test
    void descontarStockUsaElDelProductoSiElAcabadoNoControlaStock() {
        VarianteProducto variante = VarianteProducto.builder().id(UUID.randomUUID()).nombre("Nogal").build();
        Producto producto = productoConVariante(10, variante);
        ProductoRepository productoRepository = mock(ProductoRepository.class);

        serviceConMovimientos(productoRepository, mock(MovimientoStockRepository.class)).descontarStock(producto, variante, 4);

        assertEquals(6, producto.getStockActual());
        verify(productoRepository).saveAndFlush(producto);
    }

    @Test
    void descontarStockNoHaceNadaSiNadaControlaInventario() {
        VarianteProducto variante = VarianteProducto.builder().id(UUID.randomUUID()).nombre("Nogal").build();
        Producto producto = productoConVariante(null, variante);
        ProductoRepository productoRepository = mock(ProductoRepository.class);

        serviceConMovimientos(productoRepository, mock(MovimientoStockRepository.class)).descontarStock(producto, variante, 3);

        verify(productoRepository, never()).saveAndFlush(any(Producto.class));
    }

    @Test
    void reponerStockDevuelveUnidadesAlAcabadoYRegistraLaCancelacion() {
        VarianteProducto variante = VarianteProducto.builder().id(UUID.randomUUID()).nombre("Nogal").stockActual(0).disponible(false).build();
        Producto producto = productoConVariante(null, variante);
        producto.setDisponibilidad(EstadoDisponibilidad.AGOTADO);
        Pedido pedido = Pedido.builder().id(UUID.randomUUID()).codigo("PED-1").build();
        DetallePedido detalle = DetallePedido.builder().producto(producto).variante(variante).cantidad(3).build();
        MovimientoStockRepository movimientos = mock(MovimientoStockRepository.class);

        serviceConMovimientos(mock(ProductoRepository.class), movimientos).reponerStock(pedido, detalle);

        assertEquals(3, variante.getStockActual());
        assertEquals(Boolean.TRUE, variante.getDisponible());
        assertEquals(EstadoDisponibilidad.DISPONIBLE, producto.getDisponibilidad());
        ArgumentCaptor<MovimientoStock> captor = ArgumentCaptor.forClass(MovimientoStock.class);
        verify(movimientos).save(captor.capture());
        assertEquals(TipoMovimientoStock.CANCELACION, captor.getValue().getTipo());
        assertEquals(3, captor.getValue().getStockResultante());
        assertEquals(pedido.getId(), captor.getValue().getPedidoId());
    }

    @Test
    void reponerStockSinControlNoRegistraMovimientos() {
        Producto producto = Producto.builder().id(UUID.randomUUID()).nombre("Panel").build();
        Pedido pedido = Pedido.builder().id(UUID.randomUUID()).codigo("PED-2").build();
        DetallePedido detalle = DetallePedido.builder().producto(producto).cantidad(1).build();
        MovimientoStockRepository movimientos = mock(MovimientoStockRepository.class);

        serviceConMovimientos(mock(ProductoRepository.class), movimientos).reponerStock(pedido, detalle);

        verify(movimientos, never()).save(any(MovimientoStock.class));
    }

    @Test
    void registrarMovimientosDeCompraAnotaSoloLosDetallesConStockControlado() {
        VarianteProducto controlada = VarianteProducto.builder().id(UUID.randomUUID()).nombre("Nogal").stockActual(7).build();
        Producto conVariante = productoConVariante(null, controlada);
        Producto sinControl = Producto.builder().id(UUID.randomUUID()).nombre("Cielo raso").build();
        Producto conStock = Producto.builder().id(UUID.randomUUID()).nombre("Rollo").stockActual(9).build();
        Pedido pedido = Pedido.builder().id(UUID.randomUUID()).codigo("PED-3").build();
        pedido.addDetalle(DetallePedido.builder().producto(conVariante).variante(controlada).cantidad(1).build());
        pedido.addDetalle(DetallePedido.builder().producto(sinControl).cantidad(1).build());
        pedido.addDetalle(DetallePedido.builder().producto(conStock).cantidad(2).build());
        MovimientoStockRepository movimientos = mock(MovimientoStockRepository.class);

        serviceConMovimientos(mock(ProductoRepository.class), movimientos).registrarMovimientosDeCompra(pedido);

        ArgumentCaptor<MovimientoStock> captor = ArgumentCaptor.forClass(MovimientoStock.class);
        verify(movimientos, org.mockito.Mockito.times(2)).save(captor.capture());
        assertEquals(List.of(7, 9), captor.getAllValues().stream().map(MovimientoStock::getStockResultante).toList());
        assertEquals(TipoMovimientoStock.PEDIDO, captor.getAllValues().get(0).getTipo());
    }
}
