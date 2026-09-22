package com.isanorte.constructora_api.service.implementation.scraping;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.util.Locale;
import java.util.Set;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;
import com.isanorte.constructora_api.exception.ScrapingPrecioException;
import lombok.RequiredArgsConstructor;
import static com.isanorte.constructora_api.enums.EstadoComparacionPrecio.*;

/** Descarga HTML reutilizando DNS fijado y valida el dominio oficial en cada redirección. */
@Component
@RequiredArgsConstructor
public class SitioOficialClient {
    private static final Set<Integer> REDIRECTS = Set.of(301, 302, 303, 307, 308);
    private static final int MAX_REDIRECTS = 3;
    private final UrlExternaPolicy urlPolicy;
    private final PaginaExternaClient client;

    public Document obtener(String url, Set<String> dominiosPermitidos) {
        try {
            URI actual = URI.create(url);
            for (int redirects = 0; ; redirects++) {
                validarDominio(actual, dominiosPermitidos);
                var destino = urlPolicy.validar(actual.toString());
                var pagina = client.obtener(destino);
                if (REDIRECTS.contains(pagina.status())) {
                    if (redirects >= MAX_REDIRECTS || pagina.location() == null) throw noDisponible();
                    URI siguiente = actual.resolve(pagina.location());
                    if ("https".equalsIgnoreCase(actual.getScheme())
                            && "http".equalsIgnoreCase(siguiente.getScheme())) throw noDisponible();
                    validarDominio(siguiente, dominiosPermitidos);
                    actual = siguiente;
                    continue;
                }
                if (pagina.status() != 200) throw noDisponible();
                return Jsoup.parse(new ByteArrayInputStream(pagina.html()), pagina.charset(), actual.toString());
            }
        } catch (IOException | IllegalArgumentException ex) {
            throw noDisponible();
        }
    }

    private static void validarDominio(URI uri, Set<String> permitidos) {
        String host = uri.getHost() == null ? "" : uri.getHost().toLowerCase(Locale.ROOT);
        if (!permitidos.contains(host)) throw new ScrapingPrecioException(BLOCKED_URL, "Dominio no permitido.");
    }

    private static ScrapingPrecioException noDisponible() {
        return new ScrapingPrecioException(SITE_UNREACHABLE, "No fue posible consultar esta tienda.");
    }
}
