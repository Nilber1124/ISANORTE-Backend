package com.isanorte.constructora_api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.net.URI;
import java.util.Arrays;
import java.util.List;

@Configuration
public class CorsConfig {

    private static final String DEFAULT_ALLOWED_ORIGINS =
            "http://localhost:4200,http://localhost:3000,https://isanorte-frontend.vercel.app";

    @Bean
    public CorsConfigurationSource corsConfigurationSource(
            @Value("${cors.allowed-origins:" + DEFAULT_ALLOWED_ORIGINS + "}") String allowedOriginsValue) {
        List<String> origins = parseAllowedOrigins(allowedOriginsValue);

        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(origins);
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type", "Accept", "Origin", "X-Requested-With"));
        config.setAllowCredentials(false);
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    static List<String> parseAllowedOrigins(String configuredOrigins) {
        if (configuredOrigins == null || configuredOrigins.isBlank()) {
            throw new IllegalArgumentException("cors.allowed-origins debe contener al menos un origen explícito");
        }

        List<String> origins = Arrays.stream(configuredOrigins.split(","))
                .map(String::trim)
                .filter(origin -> !origin.isEmpty())
                .distinct()
                .toList();

        if (origins.isEmpty()) {
            throw new IllegalArgumentException("cors.allowed-origins debe contener al menos un origen explícito");
        }
        origins.forEach(CorsConfig::validateOrigin);
        return origins;
    }

    private static void validateOrigin(String origin) {
        if (origin.contains("*")) {
            throw new IllegalArgumentException("CORS no admite comodines; configure orígenes exactos: " + origin);
        }
        URI uri;
        try {
            uri = URI.create(origin);
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException("Origen CORS inválido: " + origin, exception);
        }
        boolean validScheme = "http".equalsIgnoreCase(uri.getScheme()) || "https".equalsIgnoreCase(uri.getScheme());
        boolean rootOrigin = (uri.getPath() == null || uri.getPath().isEmpty())
                && uri.getQuery() == null && uri.getFragment() == null && uri.getUserInfo() == null;
        if (!validScheme || uri.getHost() == null || !rootOrigin) {
            throw new IllegalArgumentException("Origen CORS inválido; use esquema, host y puerto opcional: " + origin);
        }
    }
}
