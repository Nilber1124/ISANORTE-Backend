package com.isanorte.constructora_api.service.implementation;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import com.isanorte.constructora_api.dto.request.ActivoRequest;
import com.isanorte.constructora_api.dto.request.IsadecorServicioPuntoRequest;
import com.isanorte.constructora_api.exception.ModelNotFoundException;
import com.isanorte.constructora_api.model.IsadecorServicioEmpresa;
import com.isanorte.constructora_api.model.IsadecorServicioPunto;
import com.isanorte.constructora_api.repository.IsadecorServicioEmpresaRepository;
import com.isanorte.constructora_api.repository.IsadecorServicioPuntoRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class IsadecorServicioPuntoServiceTest {

    @Mock private IsadecorServicioPuntoRepository repository;
    @Mock private IsadecorServicioEmpresaRepository isadecorServicioEmpresaRepository;

    @InjectMocks
    private IsadecorServicioPuntoService service;

    private final UUID isadecorServicioEmpresaId = UUID.randomUUID();

    @Test
    void createGuardaYDevuelveLaRespuesta() {
        IsadecorServicioEmpresa servicio = new IsadecorServicioEmpresa();
        servicio.setId(isadecorServicioEmpresaId);
        when(isadecorServicioEmpresaRepository.findById(isadecorServicioEmpresaId)).thenReturn(Optional.of(servicio));

        when(repository.save(any(IsadecorServicioPunto.class))).thenAnswer(invocation -> {
            IsadecorServicioPunto guardada = invocation.getArgument(0);
            guardada.setId(UUID.randomUUID());
            return guardada;
        });

        var response = service.create(new IsadecorServicioPuntoRequest(isadecorServicioEmpresaId, "Texto de prueba", 1));

        assertNotNull(response.id());
        verify(repository).save(any(IsadecorServicioPunto.class));
    }

    @Test
    void findAllDevuelveLaListaOrdenada() {
        IsadecorServicioPunto entity = new IsadecorServicioPunto();
        entity.setId(UUID.randomUUID());
        IsadecorServicioEmpresa servicioActual = new IsadecorServicioEmpresa();
        servicioActual.setId(isadecorServicioEmpresaId);
        entity.setServicio(servicioActual);
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
        IsadecorServicioEmpresa servicio = new IsadecorServicioEmpresa();
        servicio.setId(isadecorServicioEmpresaId);
        when(isadecorServicioEmpresaRepository.findById(isadecorServicioEmpresaId)).thenReturn(Optional.of(servicio));
        IsadecorServicioPunto entity = new IsadecorServicioPunto();
        entity.setId(UUID.randomUUID());
        IsadecorServicioEmpresa servicioActual = new IsadecorServicioEmpresa();
        servicioActual.setId(isadecorServicioEmpresaId);
        entity.setServicio(servicioActual);
        UUID id = entity.getId();
        when(repository.findById(id)).thenReturn(Optional.of(entity));
        when(repository.saveAndFlush(entity)).thenReturn(entity);

        var response = service.update(id, new IsadecorServicioPuntoRequest(isadecorServicioEmpresaId, "Texto de prueba", 1));

        assertEquals(id, response.id());
        verify(repository).saveAndFlush(entity);
    }

    @Test
    void updateLanzaNoEncontradoSiNoExiste() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ModelNotFoundException.class, () -> service.update(id, new IsadecorServicioPuntoRequest(isadecorServicioEmpresaId, "Texto de prueba", 1)));
        verify(repository, never()).saveAndFlush(any(IsadecorServicioPunto.class));
    }

    @Test
    void deleteEliminaLaEntidad() {
        IsadecorServicioPunto entity = new IsadecorServicioPunto();
        entity.setId(UUID.randomUUID());
        IsadecorServicioEmpresa servicioActual = new IsadecorServicioEmpresa();
        servicioActual.setId(isadecorServicioEmpresaId);
        entity.setServicio(servicioActual);
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
        verify(repository, never()).delete(any(IsadecorServicioPunto.class));
    }
}
