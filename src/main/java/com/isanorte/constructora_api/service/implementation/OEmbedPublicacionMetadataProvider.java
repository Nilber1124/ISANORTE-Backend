package com.isanorte.constructora_api.service.implementation;

import java.net.URI;
import java.time.Duration;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import com.isanorte.constructora_api.enums.RedPublicacion;
import com.isanorte.constructora_api.service.IPublicacionMetadataProvider;

/**
 * Lee el oEmbed público de Instagram (Meta lo ofrece sin token desde junio de 2026) y de TikTok.
 * Solo se consultan los dominios oficiales de cada red.
 */
@Service
public class OEmbedPublicacionMetadataProvider implements IPublicacionMetadataProvider {

    private static final Logger log = LoggerFactory.getLogger(OEmbedPublicacionMetadataProvider.class);
    private static final String INSTAGRAM_OEMBED = "https://graph.facebook.com/instagram_oembed";
    private static final String TIKTOK_OEMBED = "https://www.tiktok.com/oembed";

    private final RestClient client;
    private final String instagramEndpoint;
    private final String tiktokEndpoint;

    /** Constructor que usa Spring: apunta a los endpoints oficiales de cada red. */
    @Autowired
    public OEmbedPublicacionMetadataProvider() {
        this(INSTAGRAM_OEMBED, TIKTOK_OEMBED);
    }

    /** Permite apuntar a otro servidor (pruebas). */
    OEmbedPublicacionMetadataProvider(String instagramEndpoint, String tiktokEndpoint) {
        this.instagramEndpoint = instagramEndpoint;
        this.tiktokEndpoint = tiktokEndpoint;
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(Duration.ofSeconds(5));
        factory.setReadTimeout(Duration.ofSeconds(8));
        this.client = RestClient.builder().requestFactory(factory).build();
    }

    @Override
    public Optional<Metadata> fetch(RedPublicacion red, String url) {
        if (red == RedPublicacion.FACEBOOK) {
            return Optional.empty(); // el oEmbed de Facebook exige token de app: la miniatura se sube a mano
        }
        try {
            String endpoint = red == RedPublicacion.INSTAGRAM ? instagramEndpoint : tiktokEndpoint;
            URI uri = UriComponentsBuilder.fromUriString(endpoint)
                    .queryParam("url", url)
                    .queryParam("omitscript", "true")
                    .build().encode().toUri();
            @SuppressWarnings("unchecked")
            Map<String, Object> body = client.get().uri(uri).retrieve().body(Map.class);
            if (body == null) {
                return Optional.empty();
            }
            String title = text(body.get("title"));
            if (title == null) {
                String author = text(body.get("author_name"));
                title = author == null ? null : "Publicación de " + author;
            }
            return Optional.of(new Metadata(title, text(body.get("thumbnail_url"))));
        } catch (RuntimeException ex) {
            log.warn("No se pudo leer el oEmbed de {}: {}", red, ex.getMessage());
            return Optional.empty();
        }
    }

    private String text(Object value) {
        return value instanceof String s && !s.isBlank() ? s.trim() : null;
    }
}
