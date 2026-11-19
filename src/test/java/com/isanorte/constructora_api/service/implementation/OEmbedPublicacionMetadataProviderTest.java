package com.isanorte.constructora_api.service.implementation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.isanorte.constructora_api.enums.RedPublicacion;
import com.sun.net.httpserver.HttpServer;

class OEmbedPublicacionMetadataProviderTest {

    private HttpServer server;
    private String base;
    private String lastQuery;

    @BeforeEach
    void start() throws IOException {
        server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        base = "http://127.0.0.1:" + server.getAddress().getPort();
    }

    @AfterEach
    void stop() {
        server.stop(0);
    }

    private void respond(String path, int status, String body) {
        server.createContext(path, exchange -> {
            lastQuery = exchange.getRequestURI().getRawQuery();
            byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.sendResponseHeaders(status, bytes.length);
            exchange.getResponseBody().write(bytes);
            exchange.close();
        });
        server.start();
    }

    @Test
    void leeTituloYMiniaturaDelOEmbed() {
        respond("/ig", 200, "{\"title\":\"Panel WPC\",\"thumbnail_url\":\"https://cdn/x.jpg\",\"author_name\":\"isadecor\"}");
        var provider = new OEmbedPublicacionMetadataProvider(base + "/ig", base + "/tt");

        var metadata = provider.fetch(RedPublicacion.INSTAGRAM, "https://www.instagram.com/p/A1/").orElseThrow();

        assertEquals("Panel WPC", metadata.titulo());
        assertEquals("https://cdn/x.jpg", metadata.miniaturaUrl());
        assertTrue(lastQuery.contains("url=https://www.instagram.com/p/A1/") || lastQuery.contains("url=https%3A"));
    }

    @Test
    void sinTituloUsaElAutor() {
        respond("/tt", 200, "{\"author_name\":\"isadecor\",\"thumbnail_url\":\"https://cdn/t.jpg\"}");
        var provider = new OEmbedPublicacionMetadataProvider(base + "/ig", base + "/tt");

        var metadata = provider.fetch(RedPublicacion.TIKTOK, "https://www.tiktok.com/@isadecor/video/1").orElseThrow();

        assertEquals("Publicación de isadecor", metadata.titulo());
    }

    @Test
    void siLaRedFallaDevuelveVacioSinLanzar() {
        respond("/ig", 400, "{\"error\":{\"message\":\"Media Not Found\"}}");
        var provider = new OEmbedPublicacionMetadataProvider(base + "/ig", base + "/tt");

        assertTrue(provider.fetch(RedPublicacion.INSTAGRAM, "https://www.instagram.com/p/A1/").isEmpty());
    }
}
