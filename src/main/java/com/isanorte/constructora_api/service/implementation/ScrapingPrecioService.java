package com.isanorte.constructora_api.service.implementation;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.util.Set;
import java.util.concurrent.Semaphore;
import org.jsoup.Jsoup;
import org.springframework.stereotype.Service;
import com.isanorte.constructora_api.exception.ScrapingPrecioException;
import com.isanorte.constructora_api.service.IScrapingPrecioService;
import com.isanorte.constructora_api.service.implementation.scraping.*;
import static com.isanorte.constructora_api.enums.EstadoComparacionPrecio.*;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ScrapingPrecioService implements IScrapingPrecioService {
    private static final int MAX_REDIRECTS = 3;
    private static final Set<Integer> REDIRECTS = Set.of(301, 302, 303, 307, 308);
    private final UrlExternaPolicy urlPolicy;
    private final PaginaExternaClient client;
    private final PrecioHtmlExtractor extractor;
    private final Semaphore slots = new Semaphore(4);

    @Override
    public PrecioExterno extraer(String url) {
        if (!slots.tryAcquire()) {
            throw new ScrapingPrecioException(SITE_UNREACHABLE,
                    "El servicio está ocupado. Inténtalo nuevamente en unos segundos.");
        }
        try {
            var destino = urlPolicy.validar(url);
            for (int redirects = 0; ; redirects++) {
                var pagina = client.obtener(destino);
                if (REDIRECTS.contains(pagina.status())) {
                    if (redirects >= MAX_REDIRECTS || pagina.location() == null) throw unreachable();
                    URI next;
                    try { next = destino.uri().resolve(pagina.location()); }
                    catch (IllegalArgumentException ex) { throw unreachable(); }
                    if (destino.uri().getScheme().equalsIgnoreCase("https")
                            && next.getScheme().equalsIgnoreCase("http")) {
                        throw new ScrapingPrecioException(BLOCKED_URL,
                                "No se permiten redirecciones de HTTPS a HTTP.");
                    }
                    destino = urlPolicy.validar(next.toString());
                    continue;
                }
                if (pagina.status() != 200) throw unreachable();
                var document = Jsoup.parse(new ByteArrayInputStream(pagina.html()), pagina.charset(),
                        destino.uri().toString());
                var precio = extractor.extraer(document);
                return new PrecioExterno(destino.uri().toString(), destino.uri().getHost(),
                        precio.nombre(), precio.precio(), precio.moneda());
            }
        } catch (IOException ex) {
            // Includes DNS/connection/read failures and timeouts; never log the sensitive URL.
            throw unreachable();
        } finally {
            slots.release();
        }
    }

    private static ScrapingPrecioException unreachable() {
        return new ScrapingPrecioException(SITE_UNREACHABLE, "No fue posible acceder a esta página.");
    }
}

