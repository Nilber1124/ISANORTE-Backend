package com.isanorte.constructora_api.service.implementation;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.isanorte.constructora_api.dto.request.DestacadoProductoRequest;
import com.isanorte.constructora_api.dto.request.ImagenProductoRequest;
import com.isanorte.constructora_api.dto.response.ProductoResponse;
import com.isanorte.constructora_api.exception.ModelNotFoundException;
import com.isanorte.constructora_api.mapper.ProductoMapper;
import com.isanorte.constructora_api.model.ImagenProducto;
import com.isanorte.constructora_api.model.Producto;
import com.isanorte.constructora_api.model.VarianteProducto;
import com.isanorte.constructora_api.repository.CategoriaProductoRepository;
import com.isanorte.constructora_api.repository.ConfiguracionCalculoRepository;
import com.isanorte.constructora_api.repository.DocumentoProductoRepository;
import com.isanorte.constructora_api.repository.EspecificacionProductoRepository;
import com.isanorte.constructora_api.repository.ImagenProductoRepository;
import com.isanorte.constructora_api.repository.ProductoRepository;
import com.isanorte.constructora_api.repository.UnidadNegocioRepository;
import com.isanorte.constructora_api.repository.VarianteProductoRepository;

class ProductoServiceTest {

    @Test
    void updateDestacadoDebePersistirSeleccionYOrden() {
        UUID id = UUID.randomUUID();
        ProductoRepository repository = mock(ProductoRepository.class);
        ProductoMapper mapper = mock(ProductoMapper.class);
        Producto producto = Producto.builder().id(id).destacado(false).ordenDestacado(0).build();
        ProductoResponse expected = null;
        when(repository.findById(id)).thenReturn(Optional.of(producto));
        when(repository.saveAndFlush(producto)).thenReturn(producto);
        when(mapper.toResponse(producto)).thenReturn(expected);
        ProductoService service = service(repository, mapper);

        ProductoResponse result = service.updateDestacado(id, new DestacadoProductoRequest(true, 3));

        assertSame(expected, result);
        org.junit.jupiter.api.Assertions.assertTrue(producto.getDestacado());
        org.junit.jupiter.api.Assertions.assertEquals(3, producto.getOrdenDestacado());
        verify(repository).saveAndFlush(producto);
    }

    @Test
    void updateDestacadoDebeFallarSinPersistirCuandoProductoNoExiste() {
        UUID id = UUID.randomUUID();
        ProductoRepository repository = mock(ProductoRepository.class);
        ProductoMapper mapper = mock(ProductoMapper.class);
        when(repository.findById(id)).thenReturn(Optional.empty());
        ProductoService service = service(repository, mapper);
        DestacadoProductoRequest request = new DestacadoProductoRequest(true, 0);

        assertThrows(ModelNotFoundException.class, () -> service.updateDestacado(id, request));

        verify(repository, never()).saveAndFlush(org.mockito.ArgumentMatchers.any());
    }

    private Producto productoConAcabado(UUID id) {
        Producto producto = Producto.builder().id(id).build();
        producto.addVariante(VarianteProducto.builder().id(UUID.randomUUID()).sku("PISO-NOGAL").nombre("Nogal").build());
        return producto;
    }

    private ImagenProducto crearImagen(Producto producto, String varianteSku) {
        UUID id = producto.getId();
        ProductoRepository repository = mock(ProductoRepository.class);
        ProductoMapper mapper = mock(ProductoMapper.class);
        ImagenProductoRequest request = new ImagenProductoRequest("https://img/a.webp", null, false, 0, varianteSku);
        ImagenProducto imagen = ImagenProducto.builder().url("https://img/a.webp").build();
        when(repository.findById(id)).thenReturn(Optional.of(producto));
        when(mapper.toImagenEntity(request)).thenReturn(imagen);
        service(repository, mapper).createImagen(id, request);
        return imagen;
    }

    @Test
    void createImagenConSkuDeAcabadoLaVinculaConEseAcabado() {
        Producto producto = productoConAcabado(UUID.randomUUID());

        ImagenProducto imagen = crearImagen(producto, " piso-nogal ");

        org.junit.jupiter.api.Assertions.assertSame(producto.getVariantes().get(0), imagen.getVarianteProducto());
    }

    @Test
    void createImagenSinSkuQuedaComoImagenGeneral() {
        Producto producto = productoConAcabado(UUID.randomUUID());

        org.junit.jupiter.api.Assertions.assertNull(crearImagen(producto, null).getVarianteProducto());
        org.junit.jupiter.api.Assertions.assertNull(crearImagen(producto, "  ").getVarianteProducto());
    }

    @Test
    void createImagenConSkuDeOtroProductoSeRechaza() {
        Producto producto = productoConAcabado(UUID.randomUUID());

        assertThrows(IllegalArgumentException.class, () -> crearImagen(producto, "OTRO-SKU"));
    }

    private ImagenProducto actualizarImagen(Producto producto, ImagenProducto imagen, String varianteSku) {
        ProductoRepository repository = mock(ProductoRepository.class);
        ProductoMapper mapper = mock(ProductoMapper.class);
        ImagenProductoRepository imagenRepository = mock(ImagenProductoRepository.class);
        imagen.setProducto(producto);
        UUID imagenId = UUID.randomUUID();
        when(repository.findById(producto.getId())).thenReturn(Optional.of(producto));
        when(imagenRepository.findById(imagenId)).thenReturn(Optional.of(imagen));
        ProductoService service = new ProductoService(repository, mock(UnidadNegocioRepository.class),
                mock(CategoriaProductoRepository.class), mock(VarianteProductoRepository.class), imagenRepository,
                mock(EspecificacionProductoRepository.class), mock(DocumentoProductoRepository.class),
                mock(ConfiguracionCalculoRepository.class), mapper);
        service.updateImagen(producto.getId(), imagenId,
                new ImagenProductoRequest("https://img/a.webp", null, false, 0, varianteSku));
        return imagen;
    }

    @Test
    void updateImagenSinSkuConservaElAcabadoYConSkuVacioLoQuita() {
        Producto producto = productoConAcabado(UUID.randomUUID());
        VarianteProducto acabado = producto.getVariantes().get(0);
        ImagenProducto imagen = ImagenProducto.builder().url("https://img/a.webp").varianteProducto(acabado).build();

        org.junit.jupiter.api.Assertions.assertSame(acabado, actualizarImagen(producto, imagen, null).getVarianteProducto());
        org.junit.jupiter.api.Assertions.assertNull(actualizarImagen(producto, imagen, "").getVarianteProducto());
    }

    private ProductoService service(ProductoRepository repository, ProductoMapper mapper) {
        return new ProductoService(repository, mock(UnidadNegocioRepository.class),
                mock(CategoriaProductoRepository.class), mock(VarianteProductoRepository.class),
                mock(ImagenProductoRepository.class), mock(EspecificacionProductoRepository.class),
                mock(DocumentoProductoRepository.class), mock(ConfiguracionCalculoRepository.class), mapper);
    }
}
