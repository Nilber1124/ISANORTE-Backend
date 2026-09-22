package com.isanorte.constructora_api.service.implementation.scraping;

import java.io.*;
import java.net.*;
import java.util.Locale;
import org.apache.hc.client5.http.DnsResolver;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.config.ConnectionConfig;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.util.Timeout;
import org.springframework.stereotype.Component;
import com.isanorte.constructora_api.exception.ScrapingPrecioException;
import static com.isanorte.constructora_api.enums.EstadoComparacionPrecio.*;

@Component
public class PaginaExternaClient {
    static final int MAX_BYTES = 1_048_576;
    private static final Timeout TIMEOUT = Timeout.ofSeconds(5);
    public record Pagina(int status, String location, byte[] html, String charset) {}

    public Pagina obtener(UrlExternaPolicy.Destino destino) throws IOException {
        // No second DNS lookup: the socket uses exactly the validated addresses.
        // The original hostname is retained for Host, TLS SNI and certificate verification.
        var manager = PoolingHttpClientConnectionManagerBuilder.create()
                .setDnsResolver(dnsFijado(destino))
                .setDefaultConnectionConfig(ConnectionConfig.custom()
                        .setConnectTimeout(TIMEOUT).setSocketTimeout(TIMEOUT).build()).build();
        try (var client = HttpClients.custom().setConnectionManager(manager)
                .disableRedirectHandling().disableAutomaticRetries().disableCookieManagement()
                .disableContentCompression()
                .setUserAgent("ISADECOR-PriceComparison/1.0")
                .setDefaultRequestConfig(RequestConfig.custom().setResponseTimeout(TIMEOUT)
                        .setConnectionRequestTimeout(TIMEOUT).build()).build()) {
            HttpGet request = new HttpGet(destino.uri());
            request.setHeader("Accept", "text/html, application/xhtml+xml");
            request.setHeader("Accept-Encoding", "identity");
            // Hard per-hop deadline, including slow-drip responses; cancellation closes the socket.
            try (var scheduler = java.util.concurrent.Executors.newSingleThreadScheduledExecutor()) {
                var cancellation = scheduler.schedule(request::cancel, 8, java.util.concurrent.TimeUnit.SECONDS);
                try {
                    return client.execute(request, response -> leerRespuesta(response, request));
                } finally {
                    cancellation.cancel(false);
                    scheduler.shutdownNow();
                }
            }
        }
    }

    static DnsResolver dnsFijado(UrlExternaPolicy.Destino destino) {
        InetAddress[] validated = destino.direcciones().clone();
        return new DnsResolver() {
            public InetAddress[] resolve(String host) throws UnknownHostException {
                if (!host.replace("[", "").replace("]", "").equalsIgnoreCase(
                        destino.uri().getHost().replace("[", "").replace("]", ""))) {
                    throw new UnknownHostException();
                }
                return validated.clone();
            }
            public String resolveCanonicalHostname(String host) { return host; }
        };
    }

    static Pagina leerRespuesta(ClassicHttpResponse response, HttpGet request) throws IOException {
        try {
            int code = response.getCode();
            var location = response.getFirstHeader("Location");
            if (code >= 300 && code < 400) {
                request.cancel();
                return new Pagina(code, location == null ? null : location.getValue(), null, null);
            }
            if (code != 200 || response.getEntity() == null) {
                request.cancel();
                throw new ScrapingPrecioException(SITE_UNREACHABLE,
                        "No fue posible acceder a esta página.");
            }
            var entity = response.getEntity();
            var type = ContentType.parseLenient(entity.getContentType());
            String mime = type == null ? "" : type.getMimeType().toLowerCase(Locale.ROOT);
            if (!(mime.equals("text/html") || mime.equals("application/xhtml+xml"))
                    || entity.getContentLength() > MAX_BYTES
                    || (entity.getContentEncoding() != null
                        && !entity.getContentEncoding().equalsIgnoreCase("identity"))) {
                request.cancel();
                throw unsupported();
            }
            byte[] html;
            try (InputStream input = entity.getContent()) {
                html = input.readNBytes(MAX_BYTES + 1);
                if (html.length > MAX_BYTES) {
                    request.cancel();
                    throw unsupported();
                }
            }
            return new Pagina(code, null, html,
                    type.getCharset() == null ? null : type.getCharset().name());
        } catch (IllegalArgumentException ex) {
            request.cancel();
            throw unsupported();
        }
    }

    private static ScrapingPrecioException unsupported() {
        return new ScrapingPrecioException(UNSUPPORTED_CONTENT,
                "Esta página no es compatible actualmente con la comparación automática.");
    }
}
