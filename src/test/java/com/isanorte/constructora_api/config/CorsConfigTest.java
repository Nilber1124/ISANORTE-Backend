package com.isanorte.constructora_api.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

class CorsConfigTest {

    private final CorsConfig corsConfig = new CorsConfig();

    @Test
    void normalizaOrigenesExactosYEliminaDuplicados() {
        assertEquals(
                List.of("http://localhost:4200", "https://app.example.com"),
                CorsConfig.parseAllowedOrigins(
                        " http://localhost:4200,https://app.example.com,http://localhost:4200 "));
    }

    @Test
    void rechazaConfiguracionVaciaNulaOConSoloSeparadores() {
        assertThrows(IllegalArgumentException.class, () -> CorsConfig.parseAllowedOrigins(null));
        assertThrows(IllegalArgumentException.class, () -> CorsConfig.parseAllowedOrigins(""));
        assertThrows(IllegalArgumentException.class, () -> CorsConfig.parseAllowedOrigins(" , "));
    }

    @Test
    void rechazaComodinesEsquemasNoHttpYRutas() {
        assertThrows(IllegalArgumentException.class,
                () -> CorsConfig.parseAllowedOrigins("https://*.example.com"));
        assertThrows(IllegalArgumentException.class,
                () -> CorsConfig.parseAllowedOrigins("ftp://example.com"));
        assertThrows(IllegalArgumentException.class,
                () -> CorsConfig.parseAllowedOrigins("https://example.com/app"));
    }

    @Test
    void creaPoliticaSinCredencialesYConCabecerasNecesarias() {
        CorsConfigurationSource source = corsConfig.corsConfigurationSource(
                "http://localhost:4200,https://app.example.com");
        MockHttpServletRequest request = new MockHttpServletRequest("OPTIONS", "/api/archivos/subir-imagen");

        CorsConfiguration configuration = source.getCorsConfiguration(request);

        assertEquals(List.of("http://localhost:4200", "https://app.example.com"),
                configuration.getAllowedOrigins());
        assertEquals(Boolean.FALSE, configuration.getAllowCredentials());
        assertFalse(configuration.getAllowedHeaders().contains("*"));
        assertEquals(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"),
                configuration.getAllowedMethods());
    }
}
