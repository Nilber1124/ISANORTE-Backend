package com.isanorte.constructora_api.service.implementation;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.isanorte.constructora_api.dto.request.ActivoRequest;
import com.isanorte.constructora_api.dto.response.ProyectoResponse;
import com.isanorte.constructora_api.exception.ModelNotFoundException;
import com.isanorte.constructora_api.mapper.ProyectoMapper;
import com.isanorte.constructora_api.model.Proyecto;
import com.isanorte.constructora_api.repository.ProyectoRepository;
import com.isanorte.constructora_api.repository.ServicioRepository;
import com.isanorte.constructora_api.service.CloudinaryService;
import com.isanorte.constructora_api.util.BusinessTime;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;

class ProyectoServiceTest {

    @ParameterizedTest
    @ValueSource(booleans = { true, false })
    void actualizaEstadoConLaZonaHorariaDeNegocio(boolean activo) {
        ProyectoRepository proyectoRepository = mock(ProyectoRepository.class);
        ProyectoMapper proyectoMapper = mock(ProyectoMapper.class);
        Proyecto proyecto = mock(Proyecto.class);
        UUID id = UUID.randomUUID();
        ProyectoResponse response = new ProyectoResponse(id, "Proyecto", "proyecto", null, null, null, null,
                null, null, false, activo, 0, Set.of(), LocalDateTime.MIN, LocalDateTime.MIN);
        when(proyectoRepository.existsById(id)).thenReturn(true);
        when(proyectoRepository.findById(id)).thenReturn(Optional.of(proyecto));
        when(proyecto.getServicios()).thenReturn(Set.of());
        when(proyectoMapper.toResponse(proyecto)).thenReturn(response);
        ProyectoService service = service(proyectoRepository, proyectoMapper);
        LocalDateTime before = BusinessTime.now();

        ProyectoResponse result = service.updateActivo(id, new ActivoRequest(activo));

        LocalDateTime after = BusinessTime.now();
        ArgumentCaptor<LocalDateTime> timestamp = ArgumentCaptor.forClass(LocalDateTime.class);
        verify(proyectoRepository).updateActivoById(org.mockito.ArgumentMatchers.eq(id),
                org.mockito.ArgumentMatchers.eq(activo), timestamp.capture());
        assertFalse(timestamp.getValue().isBefore(before));
        assertFalse(timestamp.getValue().isAfter(after));
        assertSame(response, result);
    }

    @Test
    void noActualizaCuandoElProyectoNoExiste() {
        ProyectoRepository proyectoRepository = mock(ProyectoRepository.class);
        ProyectoMapper proyectoMapper = mock(ProyectoMapper.class);
        UUID id = UUID.randomUUID();
        when(proyectoRepository.existsById(id)).thenReturn(false);
        ProyectoService service = service(proyectoRepository, proyectoMapper);
        ActivoRequest request = new ActivoRequest(true);

        assertThrows(ModelNotFoundException.class, () -> service.updateActivo(id, request));

        verify(proyectoRepository, never()).updateActivoById(
                org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any(),
                org.mockito.ArgumentMatchers.any());
        verifyNoInteractions(proyectoMapper);
    }

    private static ProyectoService service(ProyectoRepository proyectoRepository, ProyectoMapper proyectoMapper) {
        return new ProyectoService(proyectoRepository, mock(ServicioRepository.class), proyectoMapper,
                mock(CloudinaryService.class));
    }
}
