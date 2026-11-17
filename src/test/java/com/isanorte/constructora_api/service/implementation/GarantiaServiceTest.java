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
import com.isanorte.constructora_api.dto.request.GarantiaRequest;
import com.isanorte.constructora_api.exception.ModelNotFoundException;
import com.isanorte.constructora_api.model.Garantia;
import com.isanorte.constructora_api.repository.GarantiaRepository;

@ExtendWith(MockitoExtension.class)
class GarantiaServiceTest {

    @Mock
    private GarantiaRepository repository;

    @InjectMocks
    private GarantiaService service;

    private GarantiaRequest request(Integer orden) {
        return new GarantiaRequest("Supervisión técnica", "En cada etapa.", true, true, orden);
    }

    @Test
    void createAplicaLosDatosYDevuelveLaRespuesta() {
        when(repository.save(any(Garantia.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var response = service.create(request(2));

        assertEquals(2, response.orden());
        assertEquals(true, response.activo());
        verify(repository).save(any(Garantia.class));
    }

    @Test
    void findAllUsaElOrdenEditorial() {
        when(repository.findAllByOrderByOrdenAscIdAsc()).thenReturn(List.of(new Garantia(), new Garantia()));

        assertEquals(2, service.findAll().size());
    }

    @Test
    void updateModificaLaEntidadExistente() {
        UUID id = UUID.randomUUID();
        Garantia existing = new Garantia();
        when(repository.findById(id)).thenReturn(Optional.of(existing));
        when(repository.saveAndFlush(existing)).thenReturn(existing);

        var response = service.update(id, request(5));

        assertEquals(5, response.orden());
    }

    @Test
    void updateActivoCambiaSoloElEstado() {
        UUID id = UUID.randomUUID();
        Garantia existing = new Garantia();
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
        verify(repository, never()).delete(any(Garantia.class));
    }

    @Test
    void deleteEliminaLaEntidadExistente() {
        UUID id = UUID.randomUUID();
        Garantia existing = new Garantia();
        when(repository.findById(id)).thenReturn(Optional.of(existing));

        service.delete(id);

        verify(repository).delete(existing);
    }
}
