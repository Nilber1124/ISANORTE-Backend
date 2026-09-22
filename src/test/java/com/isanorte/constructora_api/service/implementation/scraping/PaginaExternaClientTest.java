package com.isanorte.constructora_api.service.implementation.scraping;

import java.io.ByteArrayInputStream;
import java.net.*;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.io.entity.ByteArrayEntity;
import org.apache.hc.core5.http.io.entity.InputStreamEntity;
import org.apache.hc.core5.http.message.BasicClassicHttpResponse;
import org.junit.jupiter.api.Test;
import com.isanorte.constructora_api.exception.ScrapingPrecioException;
import static org.junit.jupiter.api.Assertions.*;
import static com.isanorte.constructora_api.enums.EstadoComparacionPrecio.*;

class PaginaExternaClientTest {
    @Test void dnsFijadoNoVuelveAResolverYNoAdmiteOtroHost() throws Exception {
        var addresses = new InetAddress[]{InetAddress.getByName("93.184.216.34")};
        var dns = PaginaExternaClient.dnsFijado(
                new UrlExternaPolicy.Destino(URI.create("https://tienda.example/p"), addresses));
        addresses[0] = InetAddress.getByName("127.0.0.1");
        assertEquals("93.184.216.34", dns.resolve("tienda.example")[0].getHostAddress());
        assertThrows(UnknownHostException.class, () -> dns.resolve("otro.example"));
    }

    @Test void rechazaPdfSinLeerElCuerpo() {
        var response = new BasicClassicHttpResponse(200);
        response.setEntity(new ByteArrayEntity(new byte[]{1, 2}, ContentType.APPLICATION_PDF));
        var request = new HttpGet("https://tienda.example/p");
        assertEquals(UNSUPPORTED_CONTENT, assertThrows(ScrapingPrecioException.class,
                () -> PaginaExternaClient.leerRespuesta(response, request)).getEstado());
        assertTrue(request.isCancelled());
    }

    @Test void limitaCuerpoInclusoSinContentLength() {
        var response = new BasicClassicHttpResponse(200);
        response.setEntity(new InputStreamEntity(
                new ByteArrayInputStream(new byte[PaginaExternaClient.MAX_BYTES + 1]), -1, ContentType.TEXT_HTML));
        var request = new HttpGet("https://tienda.example/p");
        assertEquals(UNSUPPORTED_CONTENT, assertThrows(ScrapingPrecioException.class,
                () -> PaginaExternaClient.leerRespuesta(response, request)).getEstado());
        assertTrue(request.isCancelled());
    }

    @Test void rechazaRespuestaDemasiadoGrandePorCabecera() {
        var response = new BasicClassicHttpResponse(200);
        response.setEntity(new InputStreamEntity(new ByteArrayInputStream(new byte[0]),
                PaginaExternaClient.MAX_BYTES + 1, ContentType.TEXT_HTML));
        assertEquals(UNSUPPORTED_CONTENT, assertThrows(ScrapingPrecioException.class,
                () -> PaginaExternaClient.leerRespuesta(response, new HttpGet("https://tienda.example/p"))).getEstado());
    }

    @Test void htmlControladoEsAceptado() throws Exception {
        var response = new BasicClassicHttpResponse(200);
        byte[] html = "<html><body>Mesa</body></html>".getBytes(java.nio.charset.StandardCharsets.UTF_8);
        response.setEntity(new ByteArrayEntity(html, ContentType.TEXT_HTML));
        assertArrayEquals(html, PaginaExternaClient.leerRespuesta(response,
                new HttpGet("https://tienda.example/p")).html());
    }

    @Test void errorHttpEsEstadoEsperado() {
        assertEquals(SITE_UNREACHABLE, assertThrows(ScrapingPrecioException.class,
                () -> PaginaExternaClient.leerRespuesta(new BasicClassicHttpResponse(403),
                        new HttpGet("https://tienda.example/p"))).getEstado());
    }
}

