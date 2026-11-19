package com.isanorte.constructora_api.service.implementation;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import com.isanorte.constructora_api.dto.request.ActivoRequest;
import com.isanorte.constructora_api.dto.request.IsadecorServicioEmpresaRequest;
import com.isanorte.constructora_api.exception.ModelNotFoundException;
import com.isanorte.constructora_api.model.IsadecorServicioEmpresa;
import com.isanorte.constructora_api.model.UnidadNegocio;
import com.isanorte.constructora_api.repository.IsadecorServicioEmpresaRepository;
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
class IsadecorServicioEmpresaServiceTest {

    @Mock private IsadecorServicioEmpresaRepository repository;
    @Mock private UnidadNegocioRepository unidadRepository;

    @InjectMocks
    private IsadecorServicioEmpresaService service;

    private final UUID unidadId = UUID.randomUUID();

    @Test
    void createGuardaYDevuelveLaRespuesta() {
        UnidadNegocio unidad = new UnidadNegocio();
        unidad.setId(unidadId);
        when(unidadRepository.findById(unidadId)).thenReturn(Optional.of(unidad));
        when(repository.existsByUnidadNegocioId(unidadId)).thenReturn(false);
        when(repository.save(any(IsadecorServicioEmpresa.class))).thenAnswer(invocation -> {
            IsadecorServicioEmpresa guardada = invocation.getArgument(0);
            guardada.setId(UUID.randomUUID());
            return guardada;
        });

        var response = service.create(new IsadecorServicioEmpresaRequest(unidadId, "Texto de prueba", "Texto de prueba", "Texto de prueba", "Texto de prueba", "/ruta", true));

        assertNotNull(response.id());
        verify(repository).save(any(IsadecorServicioEmpresa.class));
    }

    @Test
    void createRechazaDuplicadosPorUnidad() {
        UnidadNegocio unidad = new UnidadNegocio();
        unidad.setId(unidadId);
        when(unidadRepository.findById(unidadId)).thenReturn(Optional.of(unidad));
        when(repository.existsByUnidadNegocioId(unidadId)).thenReturn(true);

        assertThrows(IllegalStateException.class, () -> service.create(new IsadecorServicioEmpresaRequest(unidadId, "Texto de prueba", "Texto de prueba", "Texto de prueba", "Texto de prueba", "/ruta", true)));
        verify(repository, never()).save(any(IsadecorServicioEmpresa.class));
    }

    @Test
    void findAllDevuelveLaListaOrdenada() {
        IsadecorServicioEmpresa entity = new IsadecorServicioEmpresa();
        entity.setId(UUID.randomUUID());
        UnidadNegocio unidadActual = new UnidadNegocio();
        unidadActual.setId(unidadId);
        entity.setUnidadNegocio(unidadActual);
        when(repository.findAllByOrderByFechaCreacionAscIdAsc()).thenReturn(List.of(entity));

        assertEquals(1, service.findAll().size());
    }

    @Test
    void findAllDevuelveListaVaciaSinRegistros() {
        when(repository.findAllByOrderByFechaCreacionAscIdAsc()).thenReturn(List.of());

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
        IsadecorServicioEmpresa entity = new IsadecorServicioEmpresa();
        entity.setId(UUID.randomUUID());
        UnidadNegocio unidadActual = new UnidadNegocio();
        unidadActual.setId(unidadId);
        entity.setUnidadNegocio(unidadActual);
        UUID id = entity.getId();
        when(repository.findById(id)).thenReturn(Optional.of(entity));
        when(repository.saveAndFlush(entity)).thenReturn(entity);

        var response = service.update(id, new IsadecorServicioEmpresaRequest(unidadId, "Texto de prueba", "Texto de prueba", "Texto de prueba", "Texto de prueba", "/ruta", true));

        assertEquals(id, response.id());
        verify(repository).saveAndFlush(entity);
    }

    @Test
    void updateLanzaNoEncontradoSiNoExiste() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ModelNotFoundException.class, () -> service.update(id, new IsadecorServicioEmpresaRequest(unidadId, "Texto de prueba", "Texto de prueba", "Texto de prueba", "Texto de prueba", "/ruta", true)));
        verify(repository, never()).saveAndFlush(any(IsadecorServicioEmpresa.class));
    }

    @Test
    void deleteEliminaLaEntidad() {
        IsadecorServicioEmpresa entity = new IsadecorServicioEmpresa();
        entity.setId(UUID.randomUUID());
        UnidadNegocio unidadActual = new UnidadNegocio();
        unidadActual.setId(unidadId);
        entity.setUnidadNegocio(unidadActual);
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
        verify(repository, never()).delete(any(IsadecorServicioEmpresa.class));
    }

    @Test
    void updateActivoCambiaSoloElEstado() {
        IsadecorServicioEmpresa entity = new IsadecorServicioEmpresa();
        entity.setId(UUID.randomUUID());
        UnidadNegocio unidadActual = new UnidadNegocio();
        unidadActual.setId(unidadId);
        entity.setUnidadNegocio(unidadActual);
        entity.setActivo(true);
        UUID id = entity.getId();
        when(repository.findById(id)).thenReturn(Optional.of(entity));
        when(repository.saveAndFlush(entity)).thenReturn(entity);

        var response = service.updateActivo(id, new ActivoRequest(false));

        assertFalse(response.activo());
    }
}
