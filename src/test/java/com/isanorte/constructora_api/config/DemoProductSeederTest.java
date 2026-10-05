package com.isanorte.constructora_api.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.ApplicationArguments;
import org.springframework.core.io.DefaultResourceLoader;

import com.isanorte.constructora_api.enums.EstadoPublicacion;
import com.isanorte.constructora_api.model.CategoriaProducto;
import com.isanorte.constructora_api.model.Producto;
import com.isanorte.constructora_api.model.UnidadNegocio;
import com.isanorte.constructora_api.repository.CategoriaProductoRepository;
import com.isanorte.constructora_api.repository.ProductoRepository;
import com.isanorte.constructora_api.repository.UnidadNegocioRepository;

import tools.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
class DemoProductSeederTest {
    @Mock private ProductoRepository productoRepository;
    @Mock private CategoriaProductoRepository categoriaRepository;
    @Mock private UnidadNegocioRepository unidadRepository;

    private UnidadNegocio unit;
    private DemoProductSeeder seeder;

    @BeforeEach
    void setUp() {
        unit = UnidadNegocio.builder().id(UUID.randomUUID()).slug("isadecor").nombre("ISADECOR").build();
        seeder = new DemoProductSeeder(
                productoRepository, categoriaRepository, unidadRepository,
                new DefaultResourceLoader(), new ObjectMapper());
        when(unidadRepository.findBySlug("isadecor")).thenReturn(Optional.of(unit));
    }

    @Test
    void loadsCompletePublishedCatalogFromJson() throws Exception {
        when(categoriaRepository.findByActivoTrueOrderByOrdenAsc()).thenReturn(categories());
        when(productoRepository.findBySku(any())).thenReturn(Optional.empty());
        when(productoRepository.findBySlug(any())).thenReturn(Optional.empty());

        seeder.run(mock(ApplicationArguments.class));

        ArgumentCaptor<Producto> captor = ArgumentCaptor.forClass(Producto.class);
        verify(productoRepository, org.mockito.Mockito.times(26)).save(captor.capture());
        List<Producto> products = captor.getAllValues();
        assertThat(products).allMatch(product -> product.getEstado() == EstadoPublicacion.PUBLICADO);
        assertThat(products).allMatch(product -> product.getUnidadNegocio() == unit);
        assertThat(products).allMatch(product -> product.getCategorias().size() == 1);
        assertThat(products).allMatch(product -> product.getImagenes().size() == 3);
        assertThat(products).allMatch(product -> product.getVariantes().size() == 2);
        assertThat(products).allMatch(product -> product.getEspecificaciones().size() == 8);
        assertThat(products).allMatch(product -> product.getConfiguracionCalculo() != null);
        assertThat(products).filteredOn(product -> product.getPrecioAnterior() != null).hasSize(7);
        assertThat(products).extracting(Producto::getSku).doesNotHaveDuplicates();
        assertThat(products).extracting(Producto::getSlug).doesNotHaveDuplicates();
        assertThat(products).flatExtracting(Producto::getImagenes)
                .allMatch(image -> image.getUrl().startsWith("/images/") && !image.getAltText().isBlank());
    }

    @Test
    void skipsProductsWhenSkuAndSlugResolveToSameExistingProduct() throws Exception {
        when(categoriaRepository.findByActivoTrueOrderByOrdenAsc()).thenReturn(categories());
        Producto existing = Producto.builder().id(UUID.randomUUID()).build();
        when(productoRepository.findBySku(any())).thenReturn(Optional.of(existing));
        when(productoRepository.findBySlug(any())).thenReturn(Optional.of(existing));

        seeder.run(mock(ApplicationArguments.class));

        verify(productoRepository, never()).save(any());
    }

    @Test
    void failsWithoutCreatingAnythingWhenRequiredCategoryDoesNotExist() {
        when(categoriaRepository.findByActivoTrueOrderByOrdenAsc()).thenReturn(List.of());
        when(productoRepository.findBySku(any())).thenReturn(Optional.empty());
        when(productoRepository.findBySlug(any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> seeder.run(mock(ApplicationArguments.class)))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("wall-panels")
                .hasMessageContaining("no crea categorías");
        verify(productoRepository, never()).save(any());
    }

    @Test
    void failsOnNaturalKeyConflict() {
        when(categoriaRepository.findByActivoTrueOrderByOrdenAsc()).thenReturn(categories());
        when(productoRepository.findBySku(any()))
                .thenReturn(Optional.of(Producto.builder().id(UUID.randomUUID()).build()));
        when(productoRepository.findBySlug(any()))
                .thenReturn(Optional.of(Producto.builder().id(UUID.randomUUID()).build()));

        assertThatThrownBy(() -> seeder.run(mock(ApplicationArguments.class)))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Conflicto de clave natural");
        verify(productoRepository, never()).save(any());
    }

    private List<CategoriaProducto> categories() {
        return List.of(
                category("wall-panels"), category("paneles-decorativos"),
                category("revestimientos"), category("pisos"));
    }

    private CategoriaProducto category(String slug) {
        return CategoriaProducto.builder()
                .id(UUID.randomUUID()).slug(slug).nombre(slug).activo(true).unidadNegocio(unit).build();
    }
}
