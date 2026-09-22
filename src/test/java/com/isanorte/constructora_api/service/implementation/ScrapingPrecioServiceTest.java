package com.isanorte.constructora_api.service.implementation;

import java.net.*;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.isanorte.constructora_api.exception.ScrapingPrecioException;
import com.isanorte.constructora_api.service.implementation.scraping.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static com.isanorte.constructora_api.enums.EstadoComparacionPrecio.*;

class ScrapingPrecioServiceTest {
    private PaginaExternaClient client;
    private ScrapingPrecioService service;

    @BeforeEach void setup() {
        client = mock(PaginaExternaClient.class);
        var policy = new UrlExternaPolicy() {
            @Override protected InetAddress[] resolver(String host) throws UnknownHostException {
                return new InetAddress[]{InetAddress.getByName(
                        host.equals("tienda.example") ? "93.184.216.34" : host)};
            }
        };
        service = new ScrapingPrecioService(policy, client, new PrecioHtmlExtractor());
    }

    @Test void bloqueaRedireccionPrivadaAntesDeConectar() throws Exception {
        when(client.obtener(any())).thenReturn(new PaginaExternaClient.Pagina(302, "https://127.0.0.1/secreto", null, null));
        assertEquals(BLOCKED_URL, assertThrows(ScrapingPrecioException.class,
                () -> service.extraer("https://tienda.example/producto")).getEstado());
        verify(client, times(1)).obtener(any());
    }

    @Test void limitaRedirecciones() throws Exception {
        when(client.obtener(any())).thenReturn(new PaginaExternaClient.Pagina(302, "/otro", null, null));
        assertEquals(SITE_UNREACHABLE, assertThrows(ScrapingPrecioException.class,
                () -> service.extraer("https://tienda.example/producto")).getEstado());
        verify(client, times(4)).obtener(any());
    }

    @Test void bloqueaDowngradeHttps() throws Exception {
        when(client.obtener(any())).thenReturn(new PaginaExternaClient.Pagina(302, "http://tienda.example/p", null, null));
        assertEquals(BLOCKED_URL, assertThrows(ScrapingPrecioException.class,
                () -> service.extraer("https://tienda.example/producto")).getEstado());
    }

    @Test void timeoutEsErrorEsperableYLiberaCupo() throws Exception {
        when(client.obtener(any())).thenThrow(new SocketTimeoutException("sensitive transport details"));
        for (int i = 0; i < 6; i++) {
            var error = assertThrows(ScrapingPrecioException.class,
                    () -> service.extraer("https://tienda.example/producto"));
            assertEquals(SITE_UNREACHABLE, error.getEstado());
            assertFalse(error.getMessage().contains("sensitive"));
        }
        verify(client, times(6)).obtener(any());
    }

    @Test void extraeHtmlSinRedReal() throws Exception {
        when(client.obtener(any())).thenReturn(new PaginaExternaClient.Pagina(200, null,
                "<main><h1>Mesa</h1><div class='product-price'>S/ 920.00</div></main>".getBytes(StandardCharsets.UTF_8), "UTF-8"));
        var result = service.extraer("https://tienda.example/producto");
        assertEquals("920.00", result.precio().toPlainString());
        assertEquals("tienda.example", result.dominio());
    }

    @Test void conservaEstadoContenidoNoCompatible() throws Exception {
        when(client.obtener(any())).thenThrow(new ScrapingPrecioException(UNSUPPORTED_CONTENT, "No compatible"));
        assertEquals(UNSUPPORTED_CONTENT, assertThrows(ScrapingPrecioException.class,
                () -> service.extraer("https://tienda.example/producto")).getEstado());
    }
}

