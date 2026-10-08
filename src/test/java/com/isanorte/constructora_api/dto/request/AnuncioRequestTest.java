package com.isanorte.constructora_api.dto.request;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import com.isanorte.constructora_api.enums.DestinoAnuncio;
import com.isanorte.constructora_api.enums.TipoAccionAnuncio;

class AnuncioRequestTest {

    @Test
    void vigenciaEsValidaCuandoNoTieneLimites() {
        assertTrue(request(null, null).isVigenciaValida());
    }

    @Test
    void vigenciaEsValidaCuandoFinEsIgualAlInicio() {
        LocalDateTime fecha = LocalDateTime.of(2026, 10, 7, 12, 0);
        assertTrue(request(fecha, fecha).isVigenciaValida());
    }

    @Test
    void vigenciaEsInvalidaCuandoFinPrecedeAlInicio() {
        LocalDateTime inicio = LocalDateTime.of(2026, 10, 8, 12, 0);
        LocalDateTime fin = LocalDateTime.of(2026, 10, 7, 12, 0);
        assertFalse(request(inicio, fin).isVigenciaValida());
    }

    private AnuncioRequest request(LocalDateTime inicio, LocalDateTime fin) {
        return new AnuncioRequest(
                "Título", "Resumen", "Contenido", null, "https://cdn.example/image.webp",
                "Etiqueta", DestinoAnuncio.ISANORTE, TipoAccionAnuncio.RUTA_INTERNA,
                "/contacto", "Abrir", inicio, fin, true, 0);
    }
}
