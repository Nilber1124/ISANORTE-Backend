package com.isanorte.constructora_api.service.implementation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.isanorte.constructora_api.dto.request.AjusteStockRequest;
import com.isanorte.constructora_api.enums.EstadoDisponibilidad;
import com.isanorte.constructora_api.enums.EstadoStock;
import com.isanorte.constructora_api.enums.TipoMovimientoStock;
import com.isanorte.constructora_api.exception.ModelNotFoundException;
import com.isanorte.constructora_api.model.MovimientoStock;
import com.isanorte.constructora_api.model.Producto;
import com.isanorte.constructora_api.model.VarianteProducto;
import com.isanorte.constructora_api.repository.MovimientoStockRepository;
import com.isanorte.constructora_api.repository.ProductoRepository;

@ExtendWith(MockitoExtension.class)
class StockServiceTest {

    @Mock private ProductoRepository productoRepository;
    @Mock private MovimientoStockRepository movimientoRepository;
    @InjectMocks private StockService service;

    private Producto producto(Integer stock) {
        return Producto.builder().id(UUID.randomUUID()).stockActual(stock).stockMinimo(5)
                .disponibilidad(EstadoDisponibilidad.DISPONIBLE).variantes(new ArrayList<>()).build();
    }

    private VarianteProducto variante(Producto producto, Integer stock) {
        VarianteProducto variante = VarianteProducto.builder().id(UUID.randomUUID()).stockActual(stock).build();
        producto.getVariantes().add(variante);
        return variante;
    }

    private AjusteStockRequest ajuste(TipoMovimientoStock tipo, int cantidad) {
        return new AjusteStockRequest(tipo, cantidad, null);
    }

    @Test
    void entradaSumaStockYRegistraElMovimiento() {
        Producto producto = producto(10);
        when(productoRepository.findById(producto.getId())).thenReturn(Optional.of(producto));

        var response = service.ajustarProducto(producto.getId(),
                new AjusteStockRequest(TipoMovimientoStock.ENTRADA, 5, "  Reposición  "), "admin");

        assertEquals(15, response.stockActual());
        assertEquals(EstadoStock.DISPONIBLE, response.estado());
        ArgumentCaptor<MovimientoStock> captor = ArgumentCaptor.forClass(MovimientoStock.class);
        verify(movimientoRepository).save(captor.capture());
        assertEquals(TipoMovimientoStock.ENTRADA, captor.getValue().getTipo());
        assertEquals(15, captor.getValue().getStockResultante());
        assertEquals("Reposición", captor.getValue().getMotivo());
        assertEquals("admin", captor.getValue().getUsuario());
    }

    @Test
    void entradaSobreUnProductoSinControlComienzaElInventario() {
        Producto producto = producto(null);
        when(productoRepository.findById(producto.getId())).thenReturn(Optional.of(producto));

        var response = service.ajustarProducto(producto.getId(), ajuste(TipoMovimientoStock.ENTRADA, 8), null);

        assertEquals(8, response.stockActual());
        assertEquals(EstadoStock.DISPONIBLE, response.estado());
    }

    @Test
    void salidaRestaStockYMarcaQuedanBajoElMinimo() {
        Producto producto = producto(8);
        when(productoRepository.findById(producto.getId())).thenReturn(Optional.of(producto));

        var response = service.ajustarProducto(producto.getId(), ajuste(TipoMovimientoStock.SALIDA, 4), "admin");

        assertEquals(4, response.stockActual());
        assertEquals(EstadoStock.QUEDAN, response.estado());
        assertEquals(4, response.quedan());
    }

    @Test
    void salidaHastaCeroMarcaAgotado() {
        Producto producto = producto(3);
        when(productoRepository.findById(producto.getId())).thenReturn(Optional.of(producto));

        var response = service.ajustarProducto(producto.getId(), ajuste(TipoMovimientoStock.SALIDA, 3), "admin");

        assertEquals(EstadoStock.AGOTADO, response.estado());
        assertEquals(EstadoDisponibilidad.AGOTADO, producto.getDisponibilidad());
    }

    @Test
    void salidaMayorQueElStockSeRechaza() {
        Producto producto = producto(2);
        when(productoRepository.findById(producto.getId())).thenReturn(Optional.of(producto));

        assertThrows(IllegalArgumentException.class,
                () -> service.ajustarProducto(producto.getId(), ajuste(TipoMovimientoStock.SALIDA, 5), "admin"));
        verify(movimientoRepository, never()).save(any(MovimientoStock.class));
    }

    @Test
    void salidaSobreUnProductoSinControlSeRechaza() {
        Producto producto = producto(null);
        when(productoRepository.findById(producto.getId())).thenReturn(Optional.of(producto));

        assertThrows(IllegalStateException.class,
                () -> service.ajustarProducto(producto.getId(), ajuste(TipoMovimientoStock.SALIDA, 1), "admin"));
    }

    @Test
    void entradaYSalidaExigenCantidadPositiva() {
        Producto producto = producto(5);
        when(productoRepository.findById(producto.getId())).thenReturn(Optional.of(producto));

        assertThrows(IllegalArgumentException.class,
                () -> service.ajustarProducto(producto.getId(), ajuste(TipoMovimientoStock.ENTRADA, 0), "admin"));
    }

    @Test
    void ajusteFijaElStockExactoYReactivaUnProductoAgotado() {
        Producto producto = producto(0);
        producto.setDisponibilidad(EstadoDisponibilidad.AGOTADO);
        when(productoRepository.findById(producto.getId())).thenReturn(Optional.of(producto));

        var response = service.ajustarProducto(producto.getId(), ajuste(TipoMovimientoStock.AJUSTE, 20), "admin");

        assertEquals(20, response.stockActual());
        assertEquals(EstadoDisponibilidad.DISPONIBLE, producto.getDisponibilidad());
    }

    @Test
    void tiposQueNoSonManualesSeRechazan() {
        Producto producto = producto(5);
        when(productoRepository.findById(producto.getId())).thenReturn(Optional.of(producto));

        assertThrows(IllegalArgumentException.class,
                () -> service.ajustarProducto(producto.getId(), ajuste(TipoMovimientoStock.PEDIDO, 1), "admin"));
    }

    @Test
    void ajustarProductoInexistenteLanzaNoEncontrado() {
        UUID id = UUID.randomUUID();
        when(productoRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ModelNotFoundException.class,
                () -> service.ajustarProducto(id, ajuste(TipoMovimientoStock.ENTRADA, 1), "admin"));
    }

    @Test
    void ajustarVarianteActualizaSuStockYDisponibilidad() {
        Producto producto = producto(null);
        VarianteProducto variante = variante(producto, 2);
        variante(producto, 10);
        when(productoRepository.findById(producto.getId())).thenReturn(Optional.of(producto));

        var response = service.ajustarVariante(producto.getId(), variante.getId(),
                ajuste(TipoMovimientoStock.SALIDA, 2), "admin");

        assertEquals(0, variante.getStockActual());
        assertFalse(variante.getDisponible());
        assertEquals(variante.getId(), response.varianteId());
        assertEquals(10, response.quedan());
        ArgumentCaptor<MovimientoStock> captor = ArgumentCaptor.forClass(MovimientoStock.class);
        verify(movimientoRepository).save(captor.capture());
        assertEquals(variante, captor.getValue().getVariante());
    }

    @Test
    void ajustarLaUltimaVarianteEnCeroMarcaElProductoAgotado() {
        Producto producto = producto(null);
        VarianteProducto variante = variante(producto, 1);
        when(productoRepository.findById(producto.getId())).thenReturn(Optional.of(producto));

        service.ajustarVariante(producto.getId(), variante.getId(), ajuste(TipoMovimientoStock.SALIDA, 1), "admin");

        assertEquals(EstadoDisponibilidad.AGOTADO, producto.getDisponibilidad());
    }

    @Test
    void ajustarVarianteQueNoPerteneceAlProductoLanzaNoEncontrado() {
        Producto producto = producto(null);
        variante(producto, 1);
        when(productoRepository.findById(producto.getId())).thenReturn(Optional.of(producto));

        assertThrows(ModelNotFoundException.class,
                () -> service.ajustarVariante(producto.getId(), UUID.randomUUID(), ajuste(TipoMovimientoStock.ENTRADA, 1), "admin"));
    }

    @Test
    void historialMapeaLosMovimientos() {
        UUID productoId = UUID.randomUUID();
        MovimientoStock movimiento = MovimientoStock.builder().id(UUID.randomUUID()).tipo(TipoMovimientoStock.ENTRADA)
                .cantidad(5).stockResultante(15).motivo("Reposición").usuario("admin").build();
        when(productoRepository.existsById(productoId)).thenReturn(true);
        when(movimientoRepository.findByProductoIdOrderByFechaDescIdDesc(productoId)).thenReturn(List.of(movimiento));

        var historial = service.historial(productoId);

        assertEquals(1, historial.size());
        assertEquals(15, historial.get(0).stockResultante());
        assertNull(historial.get(0).varianteId());
    }

    @Test
    void historialSinMovimientosDevuelveListaVacia() {
        UUID productoId = UUID.randomUUID();
        when(productoRepository.existsById(productoId)).thenReturn(true);
        when(movimientoRepository.findByProductoIdOrderByFechaDescIdDesc(productoId)).thenReturn(List.of());

        assertTrue(service.historial(productoId).isEmpty());
    }

    @Test
    void historialDeProductoInexistenteLanzaNoEncontrado() {
        UUID productoId = UUID.randomUUID();
        when(productoRepository.existsById(productoId)).thenReturn(false);

        assertThrows(ModelNotFoundException.class, () -> service.historial(productoId));
    }
}
