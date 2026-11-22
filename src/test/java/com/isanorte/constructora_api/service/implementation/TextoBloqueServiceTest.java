package com.isanorte.constructora_api.service.implementation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.isanorte.constructora_api.dto.request.TextoBloqueRequest;
import com.isanorte.constructora_api.dto.response.TextoBloqueResponse;
import com.isanorte.constructora_api.model.TextoBloque;
import com.isanorte.constructora_api.repository.TextoBloqueRepository;

@ExtendWith(MockitoExtension.class)
class TextoBloqueServiceTest {

    @Mock private TextoBloqueRepository repository;
    @InjectMocks private TextoBloqueService service;

    @Test
    void creaLaFilaSiElBloqueNoTeniaTexto() {
        when(repository.findByClave("garantias")).thenReturn(Optional.empty());
        when(repository.saveAndFlush(any(TextoBloque.class))).thenAnswer(call -> call.getArgument(0));

        TextoBloqueResponse response = service.upsert("garantias",
                new TextoBloqueRequest(" Nuestro compromiso ", "Por qué elegir a ISANORTE", "  "));

        assertEquals("garantias", response.clave());
        assertEquals("Nuestro compromiso", response.etiqueta());
        assertEquals("Por qué elegir a ISANORTE", response.titulo());
        assertNull(response.subtitulo(), "un campo en blanco vuelve al texto por defecto");
    }

    @Test
    void actualizaLaFilaExistente() {
        TextoBloque existente = TextoBloque.builder().clave("proceso").titulo("Viejo").build();
        when(repository.findByClave("proceso")).thenReturn(Optional.of(existente));
        when(repository.saveAndFlush(existente)).thenReturn(existente);

        TextoBloqueResponse response = service.upsert("proceso", new TextoBloqueRequest(null, "Nuevo", null));

        assertEquals("Nuevo", response.titulo());
        assertEquals("Nuevo", existente.getTitulo());
    }

    @Test
    void rechazaUnBloqueDesconocido() {
        assertThrows(IllegalArgumentException.class,
                () -> service.upsert("otro", new TextoBloqueRequest(null, "x", null)));
        verify(repository, never()).saveAndFlush(any());
    }
}
