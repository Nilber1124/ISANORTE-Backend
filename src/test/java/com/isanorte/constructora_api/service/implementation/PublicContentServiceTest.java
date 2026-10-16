package com.isanorte.constructora_api.service.implementation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import com.isanorte.constructora_api.dto.response.PublicProductCardResponse;
import com.isanorte.constructora_api.dto.response.PublicProductCatalogCategoryResponse;
import com.isanorte.constructora_api.dto.response.PublicProductCatalogResponse;
import com.isanorte.constructora_api.enums.EstadoPublicacion;
import com.isanorte.constructora_api.enums.OrdenCatalogoProducto;
import com.isanorte.constructora_api.mapper.PublicProductMapper;
import com.isanorte.constructora_api.model.ConfiguracionSitio;
import com.isanorte.constructora_api.model.Empresa;
import com.isanorte.constructora_api.model.Producto;
import com.isanorte.constructora_api.model.UnidadNegocio;
import com.isanorte.constructora_api.repository.CategoriaProductoRepository;
import com.isanorte.constructora_api.repository.ConfiguracionSitioRepository;
import com.isanorte.constructora_api.repository.ContenidoPaginaRepository;
import com.isanorte.constructora_api.repository.EstadisticaEmpresaRepository;
import com.isanorte.constructora_api.repository.ProductoRepository;
import com.isanorte.constructora_api.repository.ProyectoRepository;
import com.isanorte.constructora_api.repository.RedSocialRepository;
import com.isanorte.constructora_api.repository.ResenaProductoRepository;
import com.isanorte.constructora_api.repository.SeccionLandingRepository;
import com.isanorte.constructora_api.repository.SeoPaginaRepository;
import com.isanorte.constructora_api.repository.ServicioRepository;
import com.isanorte.constructora_api.repository.UnidadNegocioRepository;

@ExtendWith(MockitoExtension.class)
class PublicContentServiceTest {

    @Mock private ConfiguracionSitioRepository configuracionRepository;
    @Mock private SeccionLandingRepository seccionRepository;
    @Mock private ServicioRepository servicioRepository;
    @Mock private ProyectoRepository proyectoRepository;
    @Mock private ProductoRepository productoRepository;
    @Mock private CategoriaProductoRepository categoriaProductoRepository;
    @Mock private ResenaProductoRepository resenaProductoRepository;
    @Mock private UnidadNegocioRepository unidadRepository;
    @Mock private ContenidoPaginaRepository contenidoRepository;
    @Mock private EstadisticaEmpresaRepository estadisticaEmpresaRepository;
    @Mock private SeoPaginaRepository seoRepository;
    @Mock private RedSocialRepository redSocialRepository;
    @Mock private PublicProductMapper publicProductMapper;

    @InjectMocks
    private PublicContentService service;

    @Test
    void rechazaPaginaNegativa() {
        assertThrows(IllegalArgumentException.class, () -> service.findProductCatalog(
                "isanorte", "unidad", -1, 12, null, null, false, false, false, OrdenCatalogoProducto.DESTACADOS));
        verifyNoInteractions(configuracionRepository);
    }

    @Test
    void rechazaTamanoDePaginaFueraDeRango() {
        assertThrows(IllegalArgumentException.class, () -> service.findProductCatalog(
                "isanorte", "unidad", 0, 0, null, null, false, false, false, OrdenCatalogoProducto.DESTACADOS));
        assertThrows(IllegalArgumentException.class, () -> service.findProductCatalog(
                "isanorte", "unidad", 0, 49, null, null, false, false, false, OrdenCatalogoProducto.DESTACADOS));
        verifyNoInteractions(configuracionRepository);
    }

    @Test
    void construyeLaPaginaDelCatalogoConLosMetadatosDeLaConsultaYElFacetDeCategorias() {
        UUID empresaId = UUID.randomUUID();
        UUID unidadId = UUID.randomUUID();
        Empresa empresa = Empresa.builder().id(empresaId).razonSocial("Razón").nombreComercial("Comercial")
                .ruc("12345678901").build();
        ConfiguracionSitio site = ConfiguracionSitio.builder().id(UUID.randomUUID()).clave("isanorte")
                .empresa(empresa).build();
        UnidadNegocio unit = UnidadNegocio.builder().id(unidadId).nombre("Unidad").slug("unidad").build();
        Producto producto = Producto.builder().id(UUID.randomUUID()).nombre("Panel").sku("SKU-1").build();

        when(configuracionRepository.findByClave("isanorte")).thenReturn(Optional.of(site));
        when(unidadRepository.findByEmpresaIdAndSlugAndActivoTrue(empresaId, "unidad")).thenReturn(Optional.of(unit));
        when(productoRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(producto), Pageable.ofSize(12), 1));
        when(productoRepository.findByIdIn(List.of(producto.getId()))).thenReturn(List.of(producto));
        PublicProductCardResponse card = new PublicProductCardResponse(
                "Panel", "SKU-1", "panel", null, "Descripción", null, null, null, null, false, 0, false, null,
                List.of());
        when(publicProductMapper.toCard(any(), any(), any())).thenReturn(card);
        List<PublicProductCatalogCategoryResponse> facet = List.of(
                new PublicProductCatalogCategoryResponse("Muros", "muros", 3L));
        when(categoriaProductoRepository.findPublicCategoryCounts(unidadId, EstadoPublicacion.PUBLICADO))
                .thenReturn(facet);

        PublicProductCatalogResponse response = service.findProductCatalog(
                "isanorte", "unidad", 0, 12, null, null, false, false, false, OrdenCatalogoProducto.DESTACADOS);

        assertEquals("Unidad", response.unidad().nombre());
        assertEquals(facet, response.categorias());
        assertEquals(1, response.productos().contenido().size());
        assertEquals(card, response.productos().contenido().get(0));
        assertEquals(0, response.productos().pagina());
        assertEquals(1, response.productos().totalElementos());
        assertEquals(1, response.productos().totalPaginas());
        verify(productoRepository).findAll(any(Specification.class), any(Pageable.class));
        verify(productoRepository, never()).findByUnidadNegocioIdAndEstadoOrderByNombreAscIdAsc(any(), any());
    }
}
