package com.isanorte.constructora_api.service.implementation;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.isanorte.constructora_api.dto.request.IsadecorServicioPuntoRequest;
import com.isanorte.constructora_api.dto.response.IsadecorServicioPuntoResponse;
import com.isanorte.constructora_api.exception.ModelNotFoundException;
import com.isanorte.constructora_api.model.IsadecorServicioEmpresa;
import com.isanorte.constructora_api.model.IsadecorServicioPunto;
import com.isanorte.constructora_api.repository.IsadecorServicioEmpresaRepository;
import java.util.ArrayList;
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

    @Mock private IsadecorServicioEmpresaRepository isadecorServicioEmpresaRepository;

    @InjectMocks
    private IsadecorServicioPuntoService service;

    private final UUID servicioId = UUID.randomUUID();

    private static IsadecorServicioEmpresa servicio(UUID id, IsadecorServicioPunto... puntos) {
        IsadecorServicioEmpresa servicio = new IsadecorServicioEmpresa();
        servicio.setId(id);
        servicio.setPuntos(new ArrayList<>(List.of(puntos)));
        return servicio;
    }

    private static IsadecorServicioPunto punto(String texto, int orden) {
        return IsadecorServicioPunto.builder().id(UUID.randomUUID()).texto(texto).orden(orden).build();
    }

    @Test
    void createAgregaElPuntoAlServicioYLoGuarda() {
        IsadecorServicioEmpresa servicio = servicio(servicioId, punto("Existente", 2));
        when(isadecorServicioEmpresaRepository.findById(servicioId)).thenReturn(Optional.of(servicio));

        IsadecorServicioPuntoResponse response =
                service.create(new IsadecorServicioPuntoRequest(servicioId, "  Nuevo punto  ", 1));

        assertNotNull(response.id());
        assertEquals(servicioId, response.servicioId());
        assertEquals("Nuevo punto", response.texto());
        assertNotNull(response.fechaCreacion());
        assertEquals(List.of("Nuevo punto", "Existente"),
                servicio.getPuntos().stream().map(IsadecorServicioPunto::getTexto).toList());
        verify(isadecorServicioEmpresaRepository).saveAndFlush(servicio);
    }

    @Test
    void createLanzaNoEncontradoSiElServicioNoExiste() {
        when(isadecorServicioEmpresaRepository.findById(servicioId)).thenReturn(Optional.empty());

        assertThrows(ModelNotFoundException.class,
                () -> service.create(new IsadecorServicioPuntoRequest(servicioId, "Texto", 0)));
        verify(isadecorServicioEmpresaRepository, never()).saveAndFlush(any());
    }

    @Test
    void findAllJuntaLosPuntosDeTodosLosServiciosOrdenados() {
        IsadecorServicioPunto segundo = punto("Segundo", 2);
        IsadecorServicioPunto primero = punto("Primero", 1);
        when(isadecorServicioEmpresaRepository.findAll()).thenReturn(List.of(
                servicio(servicioId, segundo), servicio(UUID.randomUUID(), primero)));

        List<IsadecorServicioPuntoResponse> lista = service.findAll();

        assertEquals(List.of("Primero", "Segundo"), lista.stream().map(IsadecorServicioPuntoResponse::texto).toList());
    }

    @Test
    void findAllDevuelveListaVaciaSinRegistros() {
        when(isadecorServicioEmpresaRepository.findAll()).thenReturn(List.of(servicio(servicioId)));

        assertTrue(service.findAll().isEmpty());
    }

    @Test
    void findByIdDevuelveElPuntoConSuServicio() {
        IsadecorServicioPunto p = punto("Texto", 0);
        when(isadecorServicioEmpresaRepository.findAll()).thenReturn(List.of(servicio(servicioId, p)));

        IsadecorServicioPuntoResponse response = service.findById(p.getId());

        assertEquals(p.getId(), response.id());
        assertEquals(servicioId, response.servicioId());
    }

    @Test
    void findByIdLanzaNoEncontradoSiNoExiste() {
        when(isadecorServicioEmpresaRepository.findAll()).thenReturn(List.of(servicio(servicioId)));

        assertThrows(ModelNotFoundException.class, () -> service.findById(UUID.randomUUID()));
    }

    @Test
    void updateModificaElPuntoEnElMismoServicio() {
        IsadecorServicioPunto p = punto("Viejo", 0);
        IsadecorServicioEmpresa servicio = servicio(servicioId, p);
        when(isadecorServicioEmpresaRepository.findAll()).thenReturn(List.of(servicio));

        IsadecorServicioPuntoResponse response =
                service.update(p.getId(), new IsadecorServicioPuntoRequest(servicioId, "Nuevo", 3));

        assertEquals(p.getId(), response.id());
        assertEquals("Nuevo", servicio.getPuntos().get(0).getTexto());
        assertEquals(3, servicio.getPuntos().get(0).getOrden());
        verify(isadecorServicioEmpresaRepository).saveAndFlush(servicio);
        verify(isadecorServicioEmpresaRepository, never()).findById(any());
    }

    @Test
    void updateMueveElPuntoAOtroServicio() {
        IsadecorServicioPunto p = punto("Texto", 0);
        IsadecorServicioEmpresa origen = servicio(servicioId, p);
        UUID destinoId = UUID.randomUUID();
        IsadecorServicioEmpresa destino = servicio(destinoId);
        when(isadecorServicioEmpresaRepository.findAll()).thenReturn(List.of(origen, destino));
        when(isadecorServicioEmpresaRepository.findById(destinoId)).thenReturn(Optional.of(destino));

        IsadecorServicioPuntoResponse response =
                service.update(p.getId(), new IsadecorServicioPuntoRequest(destinoId, "Texto", 0));

        assertEquals(destinoId, response.servicioId());
        assertTrue(origen.getPuntos().isEmpty());
        assertEquals(1, destino.getPuntos().size());
        verify(isadecorServicioEmpresaRepository).saveAndFlush(origen);
        verify(isadecorServicioEmpresaRepository).saveAndFlush(destino);
    }

    @Test
    void updateLanzaNoEncontradoSiNoExiste() {
        when(isadecorServicioEmpresaRepository.findAll()).thenReturn(List.of(servicio(servicioId)));

        assertThrows(ModelNotFoundException.class, () -> service.update(UUID.randomUUID(),
                new IsadecorServicioPuntoRequest(servicioId, "Texto", 1)));
        verify(isadecorServicioEmpresaRepository, never()).saveAndFlush(any());
    }

    @Test
    void deleteQuitaElPuntoDelServicio() {
        IsadecorServicioPunto borrar = punto("Borrar", 0);
        IsadecorServicioPunto queda = punto("Queda", 1);
        IsadecorServicioEmpresa servicio = servicio(servicioId, borrar, queda);
        when(isadecorServicioEmpresaRepository.findAll()).thenReturn(List.of(servicio));

        service.delete(borrar.getId());

        assertEquals(List.of(queda.getId()), servicio.getPuntos().stream().map(IsadecorServicioPunto::getId).toList());
        verify(isadecorServicioEmpresaRepository).saveAndFlush(servicio);
    }

    @Test
    void deleteLanzaNoEncontradoSiNoExiste() {
        when(isadecorServicioEmpresaRepository.findAll()).thenReturn(List.of(servicio(servicioId)));

        assertThrows(ModelNotFoundException.class, () -> service.delete(UUID.randomUUID()));
        verify(isadecorServicioEmpresaRepository, never()).saveAndFlush(any());
    }
}
