package com.isanorte.constructora_api;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.isanorte.constructora_api.enums.DestinoAnuncio;
import com.isanorte.constructora_api.enums.TipoAccionAnuncio;
import com.isanorte.constructora_api.model.Anuncio;
import com.isanorte.constructora_api.repository.AnuncioRepository;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class AnuncioPublicApiIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private AnuncioRepository anuncioRepository;

    @Test
    void publicaSoloActivosVigentesDelDestinoEnOrdenConfigurado() throws Exception {
        LocalDateTime now = LocalDateTime.now();
        anuncioRepository.save(anuncio("ISANORTE visible", DestinoAnuncio.ISANORTE, true, 2, null, null));
        anuncioRepository.save(anuncio("Ambos visible", DestinoAnuncio.AMBOS, true, 1,
                now.minusDays(1), now.plusDays(1)));
        anuncioRepository.save(anuncio("Inactivo", DestinoAnuncio.ISANORTE, false, 0, null, null));
        anuncioRepository.save(anuncio("Expirado", DestinoAnuncio.ISANORTE, true, 0,
                now.minusDays(2), now.minusDays(1)));
        anuncioRepository.save(anuncio("Futuro", DestinoAnuncio.ISANORTE, true, 0,
                now.plusDays(1), now.plusDays(2)));
        anuncioRepository.save(anuncio("Solo ISADECOR", DestinoAnuncio.ISADECOR, true, 0, null, null));
        anuncioRepository.flush();

        mockMvc.perform(get("/api/publico/anuncios/ISANORTE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].titulo").value("Ambos visible"))
                .andExpect(jsonPath("$[1].titulo").value("ISANORTE visible"));
    }

    private Anuncio anuncio(String titulo, DestinoAnuncio destino, boolean activo, int orden,
            LocalDateTime inicio, LocalDateTime fin) {
        return Anuncio.builder()
                .titulo(titulo)
                .descripcionResumida("Resumen")
                .contenidoDetallado("Detalle")
                .imagenUrl("https://cdn.example/anuncio.webp")
                .etiqueta("Promoción")
                .destino(destino)
                .tipoAccion(TipoAccionAnuncio.RUTA_INTERNA)
                .destinoAccion("/contacto")
                .textoBoton("Abrir")
                .fechaInicio(inicio)
                .fechaFin(fin)
                .activo(activo)
                .orden(orden)
                .build();
    }
}
