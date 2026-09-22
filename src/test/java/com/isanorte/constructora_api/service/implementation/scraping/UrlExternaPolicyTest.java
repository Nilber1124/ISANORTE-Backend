package com.isanorte.constructora_api.service.implementation.scraping;

import java.net.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import com.isanorte.constructora_api.exception.ScrapingPrecioException;
import static org.junit.jupiter.api.Assertions.*;
import static com.isanorte.constructora_api.enums.EstadoComparacionPrecio.*;

class UrlExternaPolicyTest {
    private final UrlExternaPolicy policy = new UrlExternaPolicy() {
        @Override protected InetAddress[] resolver(String host) throws UnknownHostException {
            return host.equals("tienda.example") ? new InetAddress[]{InetAddress.getByName("93.184.216.34")}
                    : InetAddress.getAllByName(host); // All other test inputs are numeric literals.
        }
    };

    @ParameterizedTest
    @ValueSource(strings = {"", "no es una url", "file:///etc/passwd", "ftp://tienda.example/p",
            "jar:https://tienda.example/p", "data:text/html,1", "javascript:alert(1)",
            "https://user:secret@tienda.example/p", "https://tienda.example:8443/p", "https://tienda.example/"})
    void rechazaUrlInvalida(String value) {
        assertEquals(INVALID_URL, assertThrows(ScrapingPrecioException.class, () -> policy.validar(value)).getEstado());
    }

    @ParameterizedTest
    @ValueSource(strings = {"localhost", "127.0.0.1", "0.0.0.0", "10.0.0.1", "172.16.0.1",
            "172.31.255.255", "192.168.1.1", "169.254.169.254", "100.64.0.1", "224.0.0.1",
            "198.18.0.1", "192.0.2.1", "203.0.113.1", "240.0.0.1", "[::1]", "[::]",
            "[fc00::1]", "[fe80::1]", "[ff02::1]", "[::ffff:127.0.0.1]", "[2001:db8::1]",
            "[2002:7f00:1::1]", "[64:ff9b::7f00:1]"})
    void bloqueaDestinosNoPublicos(String host) {
        assertEquals(BLOCKED_URL, assertThrows(ScrapingPrecioException.class,
                () -> policy.validar("https://" + host + "/producto")).getEstado());
    }

    @Test void validaDireccionesDnsNoSoloHostname() {
        UrlExternaPolicy privateDns = new UrlExternaPolicy() {
            @Override protected InetAddress[] resolver(String host) throws UnknownHostException {
                return new InetAddress[]{InetAddress.getByName("93.184.216.34"), InetAddress.getByName("10.0.0.1")};
            }
        };
        assertEquals(BLOCKED_URL, assertThrows(ScrapingPrecioException.class,
                () -> privateDns.validar("https://tienda.example/producto")).getEstado());
    }

    @Test void conservaIpsValidadasParaLaConexion() {
        var destino = policy.validar("https://tienda.example/producto?id=2");
        assertEquals("93.184.216.34", destino.direcciones()[0].getHostAddress());
        assertEquals("tienda.example", destino.uri().getHost());
    }
}

