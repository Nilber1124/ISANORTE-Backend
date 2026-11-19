package com.isanorte.constructora_api.service.implementation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.isanorte.constructora_api.enums.ClaveSeccionProductos;
import com.isanorte.constructora_api.enums.EstadoDisponibilidad;
import com.isanorte.constructora_api.enums.ModoSeccionProductos;
import com.isanorte.constructora_api.exception.ModelNotFoundException;
import com.isanorte.constructora_api.model.ConfiguracionSitio;
import com.isanorte.constructora_api.model.Empresa;
import com.isanorte.constructora_api.model.IsadecorSeccionExcluido;
import com.isanorte.constructora_api.model.IsadecorSeccionFijado;
import com.isanorte.constructora_api.model.IsadecorSeccionProductos;
import com.isanorte.constructora_api.model.Producto;
import com.isanorte.constructora_api.model.UnidadNegocio;
import com.isanorte.constructora_api.repository.ConfiguracionSitioRepository;
import com.isanorte.constructora_api.repository.CotizacionRepository;
import com.isanorte.constructora_api.repository.IsadecorCampanaRepository;
import com.isanorte.constructora_api.repository.IsadecorCintaRepository;
import com.isanorte.constructora_api.repository.IsadecorHeroRepository;
import com.isanorte.constructora_api.repository.IsadecorPromocionRepository;
import com.isanorte.constructora_api.repository.IsadecorSeccionExcluidoRepository;
import com.isanorte.constructora_api.repository.IsadecorSeccionFijadoRepository;
import com.isanorte.constructora_api.repository.IsadecorSeccionProductosRepository;
import com.isanorte.constructora_api.repository.IsadecorPublicacionRepository;
import com.isanorte.constructora_api.repository.IsadecorServicioEmpresaRepository;
import com.isanorte.constructora_api.repository.IsadecorServicioPuntoRepository;
import com.isanorte.constructora_api.repository.PedidoRepository;
import com.isanorte.constructora_api.repository.ProductoRepository;
import com.isanorte.constructora_api.repository.ResenaProductoRepository;
import com.isanorte.constructora_api.repository.UnidadNegocioRepository;
import com.isanorte.constructora_api.repository.projection.DemandaProducto;

@ExtendWith(MockitoExtension.class)
class IsadecorHomeServiceTest {

    @Mock private ConfiguracionSitioRepository configuracionRepository;
    @Mock private UnidadNegocioRepository unidadRepository;
    @Mock private IsadecorHeroRepository heroRepository;
    @Mock private IsadecorCintaRepository cintaRepository;
    @Mock private IsadecorPromocionRepository promocionRepository;
    @Mock private IsadecorCampanaRepository campanaRepository;
    @Mock private IsadecorServicioEmpresaRepository servicioRepository;
    @Mock private IsadecorServicioPuntoRepository puntoRepository;
    @Mock private IsadecorSeccionProductosRepository seccionRepository;
    @Mock private IsadecorSeccionFijadoRepository fijadoRepository;
    @Mock private IsadecorSeccionExcluidoRepository excluidoRepository;
    @Mock private ProductoRepository productoRepository;
    @Mock private PedidoRepository pedidoRepository;
    @Mock private CotizacionRepository cotizacionRepository;
    @Mock private ResenaProductoRepository resenaRepository;
    @Mock private IsadecorPublicacionRepository publicacionRepository;
    @InjectMocks private IsadecorHomeService service;

    private final UUID unitId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        lenient().when(excluidoRepository.findBySeccionId(any())).thenReturn(List.of());
        lenient().when(fijadoRepository.findBySeccionIdOrderByOrdenAscIdAsc(any())).thenReturn(List.of());
    }

    private Producto producto(String nombre, boolean facil) {
        return Producto.builder().id(UUID.randomUUID()).nombre(nombre).slug(nombre.toLowerCase())
                .disponibilidad(EstadoDisponibilidad.DISPONIBLE).facilInstalacion(facil).build();
    }

    private IsadecorSeccionProductos seccion(ClaveSeccionProductos clave, ModoSeccionProductos modo, int limite) {
        return IsadecorSeccionProductos.builder().id(UUID.randomUUID()).clave(clave).titulo("t").modo(modo)
                .limite(limite).ventanaDias(30).build();
    }

    private DemandaProducto demanda(Producto p, long cantidad) {
        return new DemandaProducto() {
            @Override public UUID getProductoId() { return p.getId(); }
            @Override public Long getCantidad() { return cantidad; }
        };
    }

    private Map<UUID, Long> demandaDe(Producto p, long cantidad) {
        return Map.of(p.getId(), cantidad);
    }

    @Test
    void automaticoOrdenaPorDemandaDescendente() {
        Producto a = producto("A", false);
        Producto b = producto("B", false);
        Producto c = producto("C", false);
        var seccion = seccion(ClaveSeccionProductos.TIENDA, ModoSeccionProductos.AUTOMATICO, 2);
        Map<UUID, Long> demanda = Map.of(a.getId(), 1L, b.getId(), 9L, c.getId(), 5L);

        List<Producto> resultado = service.seleccionar(seccion, List.of(a, b, c), demanda);

        assertEquals(List.of(b, c), resultado);
    }

    @Test
    void sinDemandaUsaDestacadosYLuegoNombre() {
        Producto a = producto("A", false);
        Producto b = producto("B", false);
        b.setDestacado(true);
        var seccion = seccion(ClaveSeccionProductos.TIENDA, ModoSeccionProductos.AUTOMATICO, 8);

        List<Producto> resultado = service.seleccionar(seccion, List.of(a, b), Map.of());

        assertEquals(List.of(b, a), resultado);
    }

    @Test
    void excluidosYAgotadosNuncaAparecen() {
        Producto a = producto("A", false);
        Producto b = producto("B", false);
        Producto agotado = producto("C", false);
        agotado.setDisponibilidad(EstadoDisponibilidad.AGOTADO);
        var seccion = seccion(ClaveSeccionProductos.TIENDA, ModoSeccionProductos.AUTOMATICO, 8);
        when(excluidoRepository.findBySeccionId(seccion.getId())).thenReturn(
                List.of(IsadecorSeccionExcluido.builder().seccion(seccion).producto(a).build()));

        List<Producto> resultado = service.seleccionar(seccion, List.of(a, b, agotado), Map.of());

        assertEquals(List.of(b), resultado);
    }

    @Test
    void facilesSoloIncluyeProductosDeFacilInstalacion() {
        Producto facil = producto("A", true);
        Producto otro = producto("B", false);
        var seccion = seccion(ClaveSeccionProductos.FACILES, ModoSeccionProductos.AUTOMATICO, 8);

        assertEquals(List.of(facil), service.seleccionar(seccion, List.of(facil, otro), Map.of()));
    }

    @Test
    void manualSoloMuestraLosFijadosEnSuOrden() {
        Producto a = producto("A", false);
        Producto b = producto("B", false);
        Producto c = producto("C", false);
        var seccion = seccion(ClaveSeccionProductos.TIENDA, ModoSeccionProductos.MANUAL, 8);
        when(fijadoRepository.findBySeccionIdOrderByOrdenAscIdAsc(seccion.getId())).thenReturn(List.of(
                IsadecorSeccionFijado.builder().seccion(seccion).producto(c).orden(0).build(),
                IsadecorSeccionFijado.builder().seccion(seccion).producto(a).orden(1).build()));

        assertEquals(List.of(c, a), service.seleccionar(seccion, List.of(a, b, c), demandaDe(b, 99)));
    }

    @Test
    void mixtoPoneFijadosPrimeroYCompletaPorDemandaSinDuplicar() {
        Producto a = producto("A", false);
        Producto b = producto("B", false);
        Producto c = producto("C", false);
        var seccion = seccion(ClaveSeccionProductos.TIENDA, ModoSeccionProductos.MIXTO, 3);
        when(fijadoRepository.findBySeccionIdOrderByOrdenAscIdAsc(seccion.getId())).thenReturn(List.of(
                IsadecorSeccionFijado.builder().seccion(seccion).producto(c).orden(0).build()));
        Map<UUID, Long> demanda = Map.of(a.getId(), 1L, b.getId(), 7L, c.getId(), 50L);

        assertEquals(List.of(c, b, a), service.seleccionar(seccion, List.of(a, b, c), demanda));
    }

    @Test
    void fijadoAgotadoOExcluidoSeIgnora() {
        Producto a = producto("A", false);
        Producto agotado = producto("B", false);
        agotado.setDisponibilidad(EstadoDisponibilidad.AGOTADO);
        var seccion = seccion(ClaveSeccionProductos.TIENDA, ModoSeccionProductos.MANUAL, 8);
        when(fijadoRepository.findBySeccionIdOrderByOrdenAscIdAsc(seccion.getId())).thenReturn(List.of(
                IsadecorSeccionFijado.builder().seccion(seccion).producto(agotado).orden(0).build()));

        assertTrue(service.seleccionar(seccion, List.of(a, agotado), Map.of()).isEmpty());
    }

    @Test
    void findHomeVacioDevuelveSeccionesNulasYListasVacias() {
        var empresa = Empresa.builder().id(UUID.randomUUID()).build();
        var site = ConfiguracionSitio.builder().empresa(empresa).build();
        var unit = UnidadNegocio.builder().id(unitId).build();
        when(configuracionRepository.findByClave("isanorte")).thenReturn(Optional.of(site));
        when(unidadRepository.findByEmpresaIdAndSlugAndActivoTrue(empresa.getId(), "isadecor"))
                .thenReturn(Optional.of(unit));
        when(heroRepository.findByUnidadNegocioIdAndActivoTrueOrderByFechaCreacionAscIdAsc(unitId)).thenReturn(List.of());
        when(cintaRepository.findByUnidadNegocioIdAndActivoTrueOrderByOrdenAscIdAsc(unitId)).thenReturn(List.of());
        when(campanaRepository.findByUnidadNegocioIdAndActivoTrueOrderByFechaCreacionAscIdAsc(unitId)).thenReturn(List.of());
        when(servicioRepository.findByUnidadNegocioIdAndActivoTrueOrderByFechaCreacionAscIdAsc(unitId)).thenReturn(List.of());
        when(promocionRepository.findByUnidadNegocioIdAndActivoTrueOrderByOrdenAscIdAsc(unitId)).thenReturn(List.of());
        when(productoRepository.findByUnidadNegocioIdAndEstadoOrderByNombreAscIdAsc(any(), any())).thenReturn(List.of());
        when(seccionRepository.findByUnidadNegocioIdAndClaveAndActivoTrue(any(), any())).thenReturn(Optional.empty());
        when(pedidoRepository.demandaDesde(any())).thenReturn(List.of());
        when(cotizacionRepository.demandaDesde(any())).thenReturn(List.of());
        when(resenaRepository.findByMostrarEnHomeTrueAndProductoUnidadNegocioIdOrderByOrdenHomeAscFechaCreacionDesc(unitId))
                .thenReturn(List.of());

        var home = service.findHome("isanorte", "isadecor");

        assertNull(home.hero());
        assertNull(home.tienda());
        assertNull(home.facil());
        assertNull(home.campana());
        assertNull(home.servicios());
        assertTrue(home.cinta().isEmpty());
        assertTrue(home.promociones().isEmpty());
        assertTrue(home.resenas().isEmpty());
        assertTrue(home.comunidad().isEmpty());
    }

    @Test
    void sitioInexistenteLanzaNoEncontrado() {
        when(configuracionRepository.findByClave("x")).thenReturn(Optional.empty());

        assertThrows(ModelNotFoundException.class, () -> service.findHome("x", "isadecor"));
    }

    @Test
    void unidadInactivaLanzaNoEncontrado() {
        var empresa = Empresa.builder().id(UUID.randomUUID()).build();
        when(configuracionRepository.findByClave("isanorte"))
                .thenReturn(Optional.of(ConfiguracionSitio.builder().empresa(empresa).build()));
        when(unidadRepository.findByEmpresaIdAndSlugAndActivoTrue(empresa.getId(), "zzz")).thenReturn(Optional.empty());

        assertThrows(ModelNotFoundException.class, () -> service.findHome("isanorte", "zzz"));
    }
}
