package com.isanorte.constructora_api.service.implementation.scraping;

import java.net.*;
import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.*;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Component;
import com.isanorte.constructora_api.exception.ScrapingPrecioException;
import static com.isanorte.constructora_api.enums.EstadoComparacionPrecio.*;

@Component
public class UrlExternaPolicy {
    private final ExecutorService dnsExecutor = new ThreadPoolExecutor(0, 4, 30, TimeUnit.SECONDS,
            new SynchronousQueue<>(), Thread.ofPlatform().daemon().name("price-dns-", 0).factory());
    public record Destino(URI uri, InetAddress[] direcciones) {
        public Destino {
            direcciones = direcciones == null ? null : direcciones.clone();
        }

        @Override
        public InetAddress[] direcciones() {
            return direcciones == null ? null : direcciones.clone();
        }

        @Override
        public boolean equals(Object other) {
            return this == other || other instanceof Destino(URI otherUri, InetAddress[] otherAddresses)
                    && Objects.equals(uri, otherUri)
                    && Arrays.equals(direcciones, otherAddresses);
        }

        @Override
        public int hashCode() {
            return 31 * Objects.hashCode(uri) + Arrays.hashCode(direcciones);
        }

        @Override
        public String toString() {
            return "Destino[uri=" + uri + ", direcciones=" + Arrays.toString(direcciones) + "]";
        }
    }

    public Destino validar(String value) {
        URI uri = parseUri(value);
        String host = uri.getHost().toLowerCase(Locale.ROOT);
        validateHost(host);
        InetAddress[] addresses = resolvePublicAddresses(host);
        validateDirectProduct(uri);
        return new Destino(uri, addresses);
    }

    private static URI parseUri(String value) {
        URI uri;
        try {
            if (value == null || value.isBlank() || value.length() > 2048 || value.contains("\\")) {
                throw new IllegalArgumentException();
            }
            uri = URI.create(value.trim()).normalize();
            String scheme = uri.getScheme();
            if (scheme == null || !(scheme.equalsIgnoreCase("https") || scheme.equalsIgnoreCase("http"))
                    || uri.getHost() == null || uri.getRawUserInfo() != null || uri.getRawFragment() != null
                    || (uri.getPort() != -1 && uri.getPort() != (scheme.equalsIgnoreCase("https") ? 443 : 80))) {
                throw new IllegalArgumentException();
            }
        } catch (IllegalArgumentException ex) {
            throw new ScrapingPrecioException(INVALID_URL, "Ingresa una URL válida del producto.");
        }
        return uri;
    }

    private static void validateHost(String host) {
        if (host.endsWith(".") || host.equals("localhost") || host.endsWith(".localhost")
                || host.endsWith(".local") || host.endsWith(".internal") || host.contains("%")) {
            throw blocked();
        }
    }

    private InetAddress[] resolvePublicAddresses(String host) {
        InetAddress[] addresses;
        try {
            addresses = resolver(host);
        } catch (UnknownHostException ex) {
            throw new ScrapingPrecioException(SITE_UNREACHABLE, "No fue posible acceder a esta página.");
        }
        if (addresses.length == 0) throw blocked();
        for (InetAddress address : addresses) {
            if (!esPublica(address)) throw blocked();
        }
        return addresses;
    }

    private static void validateDirectProduct(URI uri) {
        // A bare storefront cannot identify a product. Query-based product URLs remain supported.
        if ((uri.getPath() == null || uri.getPath().isEmpty() || uri.getPath().equals("/"))
                && uri.getRawQuery() == null) {
            throw new ScrapingPrecioException(INVALID_URL,
                    "Ingresa el enlace directo del producto, no únicamente la página principal de la tienda.");
        }
    }

    // Separate seam for deterministic tests: production always resolves the actual hostname.
    protected InetAddress[] resolver(String host) throws UnknownHostException {
        Future<InetAddress[]> lookup = null;
        try {
            lookup = dnsExecutor.submit(() -> InetAddress.getAllByName(host));
            return lookup.get(2, TimeUnit.SECONDS);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new UnknownHostException();
        } catch (ExecutionException | TimeoutException | RejectedExecutionException ex) {
            throw new UnknownHostException();
        } finally {
            if (lookup != null) lookup.cancel(true);
        }
    }

    @PreDestroy
    public void cerrar() { dnsExecutor.shutdownNow(); }

    public static boolean esPublica(InetAddress address) {
        if (address.isAnyLocalAddress() || address.isLoopbackAddress() || address.isLinkLocalAddress()
                || address.isSiteLocalAddress() || address.isMulticastAddress()) return false;
        byte[] bytes = address.getAddress();
        if (bytes.length == 4) return esIpv4Publica(bytes);
        return bytes.length == 16 && esIpv6Publica(bytes);
    }

    private static boolean esIpv4Publica(byte[] bytes) {
        int a = bytes[0] & 255;
        int b = bytes[1] & 255;
        int c = bytes[2] & 255;
        return !(a == 0 || a == 10 || a == 127 || a >= 224
                    || (a == 100 && b >= 64 && b <= 127)
                    || (a == 169 && b == 254) || (a == 172 && b >= 16 && b <= 31)
                    || (a == 192 && b == 168) || (a == 192 && b == 0 && (c == 0 || c == 2))
                    || (a == 192 && b == 88 && c == 99)
                    || (a == 198 && (b == 18 || b == 19 || (b == 51 && c == 100)))
                    || (a == 203 && b == 0 && c == 113));
    }

    private static boolean esIpv6Publica(byte[] bytes) {
        // Only global unicast 2000::/3; exclude special-purpose, documentation and transition ranges.
        int a = bytes[0] & 255;
        int b = bytes[1] & 255;
        int c = bytes[2] & 255;
        int d = bytes[3] & 255;
        return (a & 0xe0) == 0x20
                && !(a == 0x20 && b == 0x01 && c < 2) // 2001::/23 (Teredo, benchmarking, ORCHID...)
                && !(a == 0x20 && b == 0x01 && c == 0x0d && d == 0xb8)
                && !(a == 0x20 && b == 0x02) // 6to4 can encode a private IPv4 destination
                && !(a == 0x3f && b == 0xff && c < 0x10); // documentation 3fff::/20
    }

    private static ScrapingPrecioException blocked() {
        return new ScrapingPrecioException(BLOCKED_URL, "La URL indicada no está permitida.");
    }
}
