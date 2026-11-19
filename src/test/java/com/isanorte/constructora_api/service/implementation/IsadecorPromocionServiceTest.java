package com.isanorte.constructora_api.service.implementation;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import com.isanorte.constructora_api.dto.request.ActivoRequest;
import com.isanorte.constructora_api.dto.request.IsadecorPromocionRequest;
import com.isanorte.constructora_api.enums.TipoPromocion;
import com.isanorte.constructora_api.exception.ModelNotFoundException;
import com.isanorte.constructora_api.model.IsadecorPromocion;
import com.isanorte.constructora_api.model.Producto;
import com.isanorte.constructora_api.model.UnidadNegocio;
import com.isanorte.constructora_api.repository.IsadecorPromocionRepository;
import com.isanorte.constructora_api.repository.ProductoRepository;
import com.isanorte.constructora_api.repository.UnidadNegocioRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class IsadecorPromocionServiceTest {

    @Mock private IsadecorPromocionRepository repository;
    @Mock private UnidadNegocioRepository unidadRepository;
    @Mock private ProductoRepository productoRepository;

    @InjectMocks
    private IsadecorPromocionService service;

    private final UUID unidadId = UUID.randomUUID();
    private final UUID productoId = UUID.randomUUID();

    @Test
    void createGuardaYDevuelveLaRespuesta() {
        UnidadNegocio unidad = new UnidadNegocio();
        unidad.setId(unidadId);
        when(unidadRepository.findById(unidadId)).thenReturn(Optional.of(unidad));
        Producto producto = new Producto();
        producto.setId(productoId);
        when(productoRepository.findById(productoId)).thenReturn(Optional.of(producto));

        when(repository.save(any(IsadecorPromocion.class))).thenAnswer(invocation -> {
            IsadecorPromocion guardada = invocation.getArgument(0);
            guardada.setId(UUID.randomUUID());
            return guardada;
        });

        var response = service.create(new IsadecorPromocionRequest(unidadId, productoId, TipoPromocion.DESCUENTO, "Texto de prueba", "https://cdn.test/img.webp", null, 1, true, true));

        assertNotNull(response.id());
        verify(repository).save(any(IsadecorPromocion.class));
    }

    @Test
    void createRechazaFechaDeFinEnElPasado() {
        UnidadNegocio unidad = new UnidadNegocio();
        unidad.setId(unidadId);
        when(unidadRepository.findById(unidadId)).thenReturn(Optional.of(unidad));
        Producto producto = new Producto();
        producto.setId(productoId);
        when(productoRepository.findById(productoId)).thenReturn(Optional.of(producto));
        var pasado = new IsadecorPromocionRequest(unidadId, productoId, TipoPromocion.DESCUENTO, "Texto de prueba", "https://cdn.test/img.webp", java.time.LocalDate.now().minusDays(2), 1, false, true);

        assertThrows(IllegalArgumentException.class, () -> service.create(pasado));
        verify(repository, never()).save(any(IsadecorPromocion.class));
    }

    @Test
    void findAllDevuelveLaListaOrdenada() {
        IsadecorPromocion entity = new IsadecorPromocion();
        entity.setId(UUID.randomUUID());
        UnidadNegocio unidadActual = new UnidadNegocio();
        unidadActual.setId(unidadId);
        entity.setUnidadNegocio(unidadActual);
        Producto productoActual = new Producto();
        productoActual.setId(productoId);
        entity.setProducto(productoActual);
        when(repository.findAllByOrderByOrdenAscIdAsc()).thenReturn(List.of(entity));

        assertEquals(1, service.findAll().size());
    }

    @Test
    void findAllDevuelveListaVaciaSinRegistros() {
        when(repository.findAllByOrderByOrdenAscIdAsc()).thenReturn(List.of());

        assertTrue(service.findAll().isEmpty());
    }

    @Test
    void findByIdLanzaNoEncontradoSiNoExiste() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ModelNotFoundException.class, () -> service.findById(id));
    }

    @Test
    void updateModificaLaEntidadExistente() {
        UnidadNegocio unidad = new UnidadNegocio();
        unidad.setId(unidadId);
        when(unidadRepository.findById(unidadId)).thenReturn(Optional.of(unidad));
        Producto producto = new Producto();
        producto.setId(productoId);
        when(productoRepository.findById(productoId)).thenReturn(Optional.of(producto));
        IsadecorPromocion entity = new IsadecorPromocion();
        entity.setId(UUID.randomUUID());
        UnidadNegocio unidadActual = new UnidadNegocio();
        unidadActual.setId(unidadId);
        entity.setUnidadNegocio(unidadActual);
        Producto productoActual = new Producto();
        productoActual.setId(productoId);
        entity.setProducto(productoActual);
        UUID id = entity.getId();
        when(repository.findById(id)).thenReturn(Optional.of(entity));
        when(repository.saveAndFlush(entity)).thenReturn(entity);

        var response = service.update(id, new IsadecorPromocionRequest(unidadId, productoId, TipoPromocion.DESCUENTO, "Texto de prueba", "https://cdn.test/img.webp", null, 1, true, true));

        assertEquals(id, response.id());
        verify(repository).saveAndFlush(entity);
    }

    @Test
    void updateLanzaNoEncontradoSiNoExiste() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ModelNotFoundException.class, () -> service.update(id, new IsadecorPromocionRequest(unidadId, productoId, TipoPromocion.DESCUENTO, "Texto de prueba", "https://cdn.test/img.webp", null, 1, true, true)));
        verify(repository, never()).saveAndFlush(any(IsadecorPromocion.class));
    }

    @Test
    void deleteEliminaLaEntidad() {
        IsadecorPromocion entity = new IsadecorPromocion();
        entity.setId(UUID.randomUUID());
        UnidadNegocio unidadActual = new UnidadNegocio();
        unidadActual.setId(unidadId);
        entity.setUnidadNegocio(unidadActual);
        Producto productoActual = new Producto();
        productoActual.setId(productoId);
        entity.setProducto(productoActual);
        UUID id = entity.getId();
        when(repository.findById(id)).thenReturn(Optional.of(entity));

        service.delete(id);

        verify(repository).delete(entity);
    }

    @Test
    void deleteLanzaNoEncontradoSiNoExiste() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ModelNotFoundException.class, () -> service.delete(id));
        verify(repository, never()).delete(any(IsadecorPromocion.class));
    }

    @Test
    void updateActivoCambiaSoloElEstado() {
        IsadecorPromocion entity = new IsadecorPromocion();
        entity.setId(UUID.randomUUID());
        UnidadNegocio unidadActual = new UnidadNegocio();
        unidadActual.setId(unidadId);
        entity.setUnidadNegocio(unidadActual);
        Producto productoActual = new Producto();
        productoActual.setId(productoId);
        entity.setProducto(productoActual);
        entity.setActivo(true);
        UUID id = entity.getId();
        when(repository.findById(id)).thenReturn(Optional.of(entity));
        when(repository.saveAndFlush(entity)).thenReturn(entity);

        var response = service.updateActivo(id, new ActivoRequest(false));

        assertFalse(response.activo());
    }
}
