package com.isanorte.constructora_api.service.implementation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.isanorte.constructora_api.dto.request.ActivoRequest;
import com.isanorte.constructora_api.dto.request.ProyectoGaleriaRequest;
import com.isanorte.constructora_api.exception.ModelNotFoundException;
import com.isanorte.constructora_api.model.ProyectoGaleria;
import com.isanorte.constructora_api.repository.ProyectoGaleriaRepository;

@ExtendWith(MockitoExtension.class)
class ProyectoGaleriaServiceTest {

    @Mock
    private ProyectoGaleriaRepository repository;

    @InjectMocks
    private ProyectoGaleriaService service;

    private ProyectoGaleriaRequest request(Integer orden) {
        return new ProyectoGaleriaRequest("casa-demo", "https://x/y.jpg", "Fachada", "FINAL", true, orden);
    }

    @Test
    void createAplicaLosDatosYDevuelveLaRespuesta() {
        when(repository.save(any(ProyectoGaleria.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var response = service.create(request(2));

        assertEquals(2, response.orden());
        assertEquals(true, response.activo());
        verify(repository).save(any(ProyectoGaleria.class));
    }

    @Test
    void findAllUsaElOrdenEditorial() {
        when(repository.findAllByOrderByOrdenAscIdAsc()).thenReturn(List.of(new ProyectoGaleria(), new ProyectoGaleria()));

        assertEquals(2, service.findAll().size());
    }

    @Test
    void updateModificaLaEntidadExistente() {
        UUID id = UUID.randomUUID();
        ProyectoGaleria existing = new ProyectoGaleria();
        when(repository.findById(id)).thenReturn(Optional.of(existing));
        when(repository.saveAndFlush(existing)).thenReturn(existing);

        var response = service.update(id, request(5));

        assertEquals(5, response.orden());
    }

    @Test
    void updateActivoCambiaSoloElEstado() {
        UUID id = UUID.randomUUID();
        ProyectoGaleria existing = new ProyectoGaleria();
        existing.setActivo(true);
        when(repository.findById(id)).thenReturn(Optional.of(existing));
        when(repository.saveAndFlush(existing)).thenReturn(existing);

        var response = service.updateActivo(id, new ActivoRequest(false));

        assertFalse(response.activo());
    }

    @Test
    void findByIdInexistenteLanzaModelNotFound() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ModelNotFoundException.class, () -> service.findById(id));
    }

    @Test
    void deleteInexistenteNoEliminaNada() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ModelNotFoundException.class, () -> service.delete(id));
        verify(repository, never()).delete(any(ProyectoGaleria.class));
    }

    @Test
    void deleteEliminaLaEntidadExistente() {
        UUID id = UUID.randomUUID();
        ProyectoGaleria existing = new ProyectoGaleria();
        when(repository.findById(id)).thenReturn(Optional.of(existing));

        service.delete(id);

        verify(repository).delete(existing);
    }
}
