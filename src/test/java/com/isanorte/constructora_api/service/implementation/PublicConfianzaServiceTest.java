package com.isanorte.constructora_api.service.implementation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.isanorte.constructora_api.exception.ModelNotFoundException;
import com.isanorte.constructora_api.model.ConfiguracionSitio;
import com.isanorte.constructora_api.model.Faq;
import com.isanorte.constructora_api.model.Garantia;
import com.isanorte.constructora_api.model.PasoProceso;
import com.isanorte.constructora_api.repository.ConfiguracionSitioRepository;
import com.isanorte.constructora_api.repository.CredencialRepository;
import com.isanorte.constructora_api.repository.FaqRepository;
import com.isanorte.constructora_api.repository.GarantiaRepository;
import com.isanorte.constructora_api.repository.MiembroEquipoRepository;
import com.isanorte.constructora_api.repository.PasoProcesoRepository;
import com.isanorte.constructora_api.repository.TestimonioRepository;

@ExtendWith(MockitoExtension.class)
class PublicConfianzaServiceTest {

    @Mock private ConfiguracionSitioRepository configuracionRepository;
    @Mock private FaqRepository faqRepository;
    @Mock private MiembroEquipoRepository equipoRepository;
    @Mock private CredencialRepository credencialRepository;
    @Mock private TestimonioRepository testimonioRepository;
    @Mock private GarantiaRepository garantiaRepository;
    @Mock private PasoProcesoRepository pasoRepository;
    @Mock private com.isanorte.constructora_api.repository.ProyectoGaleriaRepository galeriaRepository;
    @Mock private com.isanorte.constructora_api.repository.PoliticaRepository politicaRepository;

    @InjectMocks
    private PublicConfianzaService service;

    @Test
    void devuelveSoloLoActivoYOrdenadoDeCadaLista() {
        when(configuracionRepository.findByClave("isanorte")).thenReturn(Optional.of(new ConfiguracionSitio()));
        when(faqRepository.findByActivoTrueOrderByOrdenAscIdAsc()).thenReturn(List.of(
                Faq.builder().pregunta("¿Cuánto cuesta?").respuesta("Depende.").ambito("HOME").build()));
        when(garantiaRepository.findByActivoTrueOrderByOrdenAscIdAsc()).thenReturn(List.of(
                Garantia.builder().titulo("Supervisión").texto("En cada etapa").enHero(true).build()));
        when(pasoRepository.findByActivoTrueOrderByOrdenAscIdAsc()).thenReturn(List.of(
                PasoProceso.builder().titulo("Consulta").texto("Visita").build()));

        when(galeriaRepository.findByActivoTrueOrderByOrdenAscIdAsc()).thenReturn(List.of(
                com.isanorte.constructora_api.model.ProyectoGaleria.builder().proyectoSlug("casa").url("u")
                        .etiqueta("FINAL").build()));

        when(politicaRepository.findByActivoTrueOrderByOrdenAscIdAsc()).thenReturn(List.of(
                com.isanorte.constructora_api.model.Politica.builder().slug("privacidad").titulo("Privacidad")
                        .contenido("Texto").build()));

        var response = service.findConfianza("isanorte");

        assertEquals("privacidad", response.politicas().get(0).slug());
        assertEquals("FINAL", response.galeria().get(0).etiqueta());
        assertEquals(1, response.faqs().size());
        assertEquals("¿Cuánto cuesta?", response.faqs().get(0).pregunta());
        assertTrue(response.garantias().get(0).enHero());
        assertEquals("Consulta", response.pasos().get(0).titulo());
        assertTrue(response.equipo().isEmpty());
        assertTrue(response.testimonios().isEmpty());
    }

    @Test
    void sitioInexistenteLanzaModelNotFound() {
        when(configuracionRepository.findByClave("otro")).thenReturn(Optional.empty());

        assertThrows(ModelNotFoundException.class, () -> service.findConfianza("otro"));
    }
}
