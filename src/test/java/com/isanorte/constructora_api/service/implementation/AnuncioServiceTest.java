package com.isanorte.constructora_api.service.implementation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.isanorte.constructora_api.dto.request.ActivoRequest;
import com.isanorte.constructora_api.dto.request.AnuncioRequest;
import com.isanorte.constructora_api.enums.DestinoAnuncio;
import com.isanorte.constructora_api.enums.TipoAccionAnuncio;
import com.isanorte.constructora_api.exception.ModelNotFoundException;
import com.isanorte.constructora_api.model.Anuncio;
import com.isanorte.constructora_api.repository.AnuncioRepository;

@ExtendWith(MockitoExtension.class)
class AnuncioServiceTest {

    @Mock
    private AnuncioRepository anuncioRepository;

    private AnuncioService anuncioService;
    private UUID anuncioId;
    private Anuncio anuncio;

    @BeforeEach
    void setUp() {
        anuncioService = new AnuncioService(anuncioRepository);
        anuncioId = UUID.randomUUID();
        anuncio = Anuncio.builder()
                .id(anuncioId)
                .titulo("Promoción")
                .descripcionResumida("Descripción")
                .contenidoDetallado("Contenido")
                .imagenUrl("https://cdn.example/anuncio.webp")
                .etiqueta("Nuevo")
                .destino(DestinoAnuncio.ISANORTE)
                .tipoAccion(TipoAccionAnuncio.RUTA_INTERNA)
                .destinoAccion("/contacto")
                .textoBoton("Contactar")
                .activo(true)
                .orden(1)
                .build();
    }

    @Test
    void create_debeNormalizarTextosYGuardarAnuncio() {
        AnuncioRequest request = request(DestinoAnuncio.AMBOS);
        when(anuncioRepository.save(any(Anuncio.class))).thenAnswer(invocation -> {
            Anuncio value = invocation.getArgument(0);
            value.setId(anuncioId);
            return value;
        });

        var response = anuncioService.create(request);

        assertEquals(anuncioId, response.id());
        assertEquals("Promoción", response.titulo());
        assertEquals(DestinoAnuncio.AMBOS, response.destino());
        ArgumentCaptor<Anuncio> captor = ArgumentCaptor.forClass(Anuncio.class);
        verify(anuncioRepository).save(captor.capture());
        assertEquals("Promoción", captor.getValue().getTitulo());
    }

    @Test
    void updateActivo_debePersistirElNuevoEstado() {
        when(anuncioRepository.findById(anuncioId)).thenReturn(Optional.of(anuncio));
        when(anuncioRepository.saveAndFlush(anuncio)).thenReturn(anuncio);

        var response = anuncioService.updateActivo(anuncioId, new ActivoRequest(false));

        assertEquals(false, response.activo());
        verify(anuncioRepository).saveAndFlush(anuncio);
    }

    @Test
    void findPublicos_debeConsultarVigentesDelDestinoSolicitado() {
        when(anuncioRepository.findPublicosVigentes(eq(DestinoAnuncio.ISANORTE), any(LocalDateTime.class)))
                .thenReturn(List.of(anuncio));

        var result = anuncioService.findPublicos(DestinoAnuncio.ISANORTE);

        assertEquals(1, result.size());
        assertEquals(anuncioId, result.getFirst().id());
        verify(anuncioRepository).findPublicosVigentes(eq(DestinoAnuncio.ISANORTE), any(LocalDateTime.class));
    }

    @Test
    void findPublicos_debeRechazarAmbosComoSitioSolicitado() {
        assertThrows(IllegalArgumentException.class,
                () -> anuncioService.findPublicos(DestinoAnuncio.AMBOS));
        verify(anuncioRepository, never()).findPublicosVigentes(any(), any());
    }

    @Test
    void delete_debeEliminarAnuncioExistente() {
        when(anuncioRepository.findById(anuncioId)).thenReturn(Optional.of(anuncio));

        anuncioService.delete(anuncioId);

        verify(anuncioRepository).delete(anuncio);
    }

    @Test
    void findById_debeLanzarExcepcionCuandoNoExiste() {
        when(anuncioRepository.findById(anuncioId)).thenReturn(Optional.empty());

        assertThrows(ModelNotFoundException.class, () -> anuncioService.findById(anuncioId));
    }

    private AnuncioRequest request(DestinoAnuncio destino) {
        return new AnuncioRequest(
                " Promoción ", " Descripción ", " Contenido ", " ",
                "https://cdn.example/anuncio.webp", "Nuevo", destino,
                TipoAccionAnuncio.RUTA_INTERNA, "/contacto", "Contactar",
                null, null, true, 1);
    }
}

