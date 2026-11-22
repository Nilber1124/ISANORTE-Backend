package com.isanorte.constructora_api.service.implementation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
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
import com.isanorte.constructora_api.dto.request.VentaRequest;
import com.isanorte.constructora_api.dto.request.VentaRequest.DetalleVentaRequest;
import com.isanorte.constructora_api.dto.response.VentaResponse;
import com.isanorte.constructora_api.enums.CanalVenta;
import com.isanorte.constructora_api.enums.EstadoDisponibilidad;
import com.isanorte.constructora_api.enums.MetodoPagoVenta;
import com.isanorte.constructora_api.enums.TipoDocumentoCliente;
import com.isanorte.constructora_api.enums.TipoMovimientoStock;
import com.isanorte.constructora_api.exception.ModelNotFoundException;
import com.isanorte.constructora_api.model.Producto;
import com.isanorte.constructora_api.model.VarianteProducto;
import com.isanorte.constructora_api.model.Venta;
import com.isanorte.constructora_api.repository.ProductoRepository;
import com.isanorte.constructora_api.repository.VentaRepository;
import com.isanorte.constructora_api.service.IStockService;

@ExtendWith(MockitoExtension.class)
class VentaServiceTest {

    @Mock private VentaRepository ventaRepository;
    @Mock private ProductoRepository productoRepository;
    @Mock private IStockService stockService;
    @InjectMocks private VentaService service;

    private static Producto producto(Integer stock) {
        return Producto.builder().id(UUID.randomUUID()).nombre("Panel PVC").stockActual(stock).stockMinimo(5)
                .disponibilidad(EstadoDisponibilidad.DISPONIBLE).variantes(new ArrayList<>()).build();
    }

    private static VarianteProducto variante(Producto producto, Integer stock) {
        VarianteProducto variante = VarianteProducto.builder().id(UUID.randomUUID()).stockActual(stock).build();
        producto.getVariantes().add(variante);
        return variante;
    }

    private static VentaRequest request(TipoDocumentoCliente tipo, String documento, DetalleVentaRequest... detalles) {
        return new VentaRequest(CanalVenta.WHATSAPP, LocalDate.of(2026, 11, 22), " Ana Pérez ", "999111222", tipo, documento,
                null, MetodoPagoVenta.YAPE, true, "  ", List.of(detalles));
    }

    private static DetalleVentaRequest linea(UUID productoId, UUID varianteId, int cantidad, String precio) {
        return new DetalleVentaRequest(productoId, varianteId, "Panel PVC", "PV-1", cantidad, new BigDecimal(precio));
    }

    private void persistirTalCual() {
        when(ventaRepository.saveAndFlush(any(Venta.class))).thenAnswer(call -> call.getArgument(0));
        when(ventaRepository.existsByCodigo(any())).thenReturn(false);
    }

    @Test
    void registraLaVentaCalculaElTotalYDescuentaElStockDelProducto() {
        Producto panel = producto(10);
        when(productoRepository.findById(panel.getId())).thenReturn(Optional.of(panel));
        persistirTalCual();

        VentaResponse response = service.create(
                request(TipoDocumentoCliente.DNI, "12345678", linea(panel.getId(), null, 3, "12.50")), "admin");

        assertEquals(new BigDecimal("37.50"), response.total());
        assertEquals("Ana Pérez", response.clienteNombre());
        assertEquals(null, response.notas(), "una nota en blanco no se guarda");
        assertTrue(response.stockDescontado());
        assertTrue(response.codigo().startsWith("VTA-"));
        ArgumentCaptor<AjusteStockRequest> ajuste = ArgumentCaptor.forClass(AjusteStockRequest.class);
        verify(stockService).ajustarProducto(eq(panel.getId()), ajuste.capture(), eq("admin"));
        assertEquals(TipoMovimientoStock.SALIDA, ajuste.getValue().tipo());
        assertEquals(3, ajuste.getValue().cantidad());
        assertTrue(ajuste.getValue().motivo().startsWith("Venta VTA-"));
    }

    @Test
    void descuentaDelAcabadoCuandoSeElige() {
        Producto panel = producto(null);
        VarianteProducto roble = variante(panel, 8);
        when(productoRepository.findById(panel.getId())).thenReturn(Optional.of(panel));
        persistirTalCual();

        service.create(request(null, null, linea(panel.getId(), roble.getId(), 2, "40")), "admin");

        verify(stockService).ajustarVariante(eq(panel.getId()), eq(roble.getId()), any(AjusteStockRequest.class), eq("admin"));
        verify(stockService, never()).ajustarProducto(any(), any(), any());
    }

    @Test
    void noMueveStockDeUnArticuloFueraDelCatalogoNiDeUnProductoSinControl() {
        Producto sinControl = producto(null);
        when(productoRepository.findById(sinControl.getId())).thenReturn(Optional.of(sinControl));
        persistirTalCual();

        VentaResponse response = service.create(request(null, null,
                linea(null, null, 1, "100"), linea(sinControl.getId(), null, 2, "5")), "admin");

        assertEquals(new BigDecimal("110.00"), response.total());
        verifyNoInteractions(stockService);
    }

    @Test
    void exigeElAcabadoCuandoElStockSeControlaPorAcabado() {
        Producto panel = producto(null);
        variante(panel, 8);
        when(productoRepository.findById(panel.getId())).thenReturn(Optional.of(panel));
        when(ventaRepository.existsByCodigo(any())).thenReturn(false);

        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> service.create(request(null, null, linea(panel.getId(), null, 1, "10")), "admin"));
        assertTrue(error.getMessage().contains("Elige el acabado"));
        verify(ventaRepository, never()).saveAndFlush(any());
    }

    @Test
    void validaElDocumentoDelComprador() {
        assertThrows(IllegalArgumentException.class, () -> service.create(
                request(TipoDocumentoCliente.DNI, "123", linea(null, null, 1, "1")), "admin"));
        assertThrows(IllegalArgumentException.class, () -> service.create(
                request(TipoDocumentoCliente.RUC, "12345678", linea(null, null, 1, "1")), "admin"));
        assertThrows(IllegalArgumentException.class, () -> service.create(
                request(null, "12345678", linea(null, null, 1, "1")), "admin"));
        assertThrows(IllegalArgumentException.class, () -> service.create(
                request(TipoDocumentoCliente.DNI, null, linea(null, null, 1, "1")), "admin"));
        verify(ventaRepository, never()).saveAndFlush(any());
    }

    @Test
    void fallaYNoGuardaSiElProductoNoExiste() {
        UUID desconocido = UUID.randomUUID();
        when(productoRepository.findById(desconocido)).thenReturn(Optional.empty());
        when(ventaRepository.existsByCodigo(any())).thenReturn(false);

        assertThrows(ModelNotFoundException.class,
                () -> service.create(request(null, null, linea(desconocido, null, 1, "1")), "admin"));
        verify(ventaRepository, never()).saveAndFlush(any());
    }

    @Test
    void anularDevuelveElStockUnaSolaVezYReactivarLoVuelveADescontar() {
        Producto panel = producto(10);
        when(productoRepository.findById(panel.getId())).thenReturn(Optional.of(panel));
        persistirTalCual();
        service.create(request(null, null, linea(panel.getId(), null, 4, "10")), "admin");
        Venta venta = ventaGuardada();
        when(ventaRepository.findById(venta.getId())).thenReturn(Optional.of(venta));

        VentaResponse anulada = service.setAnulada(venta.getId(), true, "admin");
        assertTrue(anulada.anulada());
        assertFalse(anulada.stockDescontado());
        ArgumentCaptor<AjusteStockRequest> ajustes = ArgumentCaptor.forClass(AjusteStockRequest.class);
        verify(stockService, org.mockito.Mockito.times(2)).ajustarProducto(eq(panel.getId()), ajustes.capture(), eq("admin"));
        assertEquals(TipoMovimientoStock.ENTRADA, ajustes.getAllValues().get(1).tipo());
        assertTrue(ajustes.getAllValues().get(1).motivo().startsWith("Anulación de la venta"));

        // Anular otra vez no devuelve más stock.
        service.setAnulada(venta.getId(), true, "admin");
        verify(stockService, org.mockito.Mockito.times(2)).ajustarProducto(any(), any(), any());

        VentaResponse reactivada = service.setAnulada(venta.getId(), false, "admin");
        assertFalse(reactivada.anulada());
        assertTrue(reactivada.stockDescontado());
        verify(stockService, org.mockito.Mockito.times(3)).ajustarProducto(any(), any(), any());
    }

    @Test
    void editarDevuelveElStockAnteriorYDescuentaElNuevo() {
        Producto panel = producto(10);
        when(productoRepository.findById(panel.getId())).thenReturn(Optional.of(panel));
        persistirTalCual();
        service.create(request(null, null, linea(panel.getId(), null, 4, "10")), "admin");
        Venta venta = ventaGuardada();
        when(ventaRepository.findById(venta.getId())).thenReturn(Optional.of(venta));

        VentaResponse editada = service.update(venta.getId(),
                request(null, null, linea(panel.getId(), null, 2, "10")), "admin");

        assertEquals(new BigDecimal("20.00"), editada.total());
        ArgumentCaptor<AjusteStockRequest> ajustes = ArgumentCaptor.forClass(AjusteStockRequest.class);
        verify(stockService, org.mockito.Mockito.times(3)).ajustarProducto(eq(panel.getId()), ajustes.capture(), eq("admin"));
        assertEquals(TipoMovimientoStock.SALIDA, ajustes.getAllValues().get(0).tipo());
        assertEquals(TipoMovimientoStock.ENTRADA, ajustes.getAllValues().get(1).tipo());
        assertEquals(4, ajustes.getAllValues().get(1).cantidad());
        assertEquals(TipoMovimientoStock.SALIDA, ajustes.getAllValues().get(2).tipo());
        assertEquals(2, ajustes.getAllValues().get(2).cantidad());
    }

    @Test
    void unaVentaAnuladaNoSePuedeEditar() {
        Venta venta = Venta.builder().id(UUID.randomUUID()).anulada(true).detalles(new ArrayList<>()).build();
        when(ventaRepository.findById(venta.getId())).thenReturn(Optional.of(venta));

        assertThrows(IllegalStateException.class,
                () -> service.update(venta.getId(), request(null, null, linea(null, null, 1, "1")), "admin"));
    }

    /** La venta que se guardó en la última llamada a `saveAndFlush`. */
    private Venta ventaGuardada() {
        ArgumentCaptor<Venta> captor = ArgumentCaptor.forClass(Venta.class);
        verify(ventaRepository).saveAndFlush(captor.capture());
        Venta venta = captor.getValue();
        venta.setId(UUID.randomUUID());
        return venta;
    }
}
