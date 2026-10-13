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
import com.isanorte.constructora_api.dto.response.ProductoResponse;
import com.isanorte.constructora_api.exception.ModelNotFoundException;
import com.isanorte.constructora_api.mapper.ProductoMapper;
import com.isanorte.constructora_api.model.Producto;
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

        assertThrows(ModelNotFoundException.class,
                () -> service.updateDestacado(id, new DestacadoProductoRequest(true, 0)));

        verify(repository, never()).saveAndFlush(org.mockito.ArgumentMatchers.any());
    }

    private ProductoService service(ProductoRepository repository, ProductoMapper mapper) {
        return new ProductoService(repository, mock(UnidadNegocioRepository.class),
                mock(CategoriaProductoRepository.class), mock(VarianteProductoRepository.class),
                mock(ImagenProductoRepository.class), mock(EspecificacionProductoRepository.class),
                mock(DocumentoProductoRepository.class), mock(ConfiguracionCalculoRepository.class), mapper);
    }
}
