package com.isanorte.constructora_api.service.competitor;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.math.BigDecimal;
import java.util.Map;
import org.jsoup.Jsoup;
import org.junit.jupiter.api.Test;
import com.isanorte.constructora_api.service.implementation.scraping.PrecioHtmlExtractor;
import com.isanorte.constructora_api.service.implementation.scraping.SitioOficialClient;

class CompetidorProductoProviderTest {
    private static final ProductoReferencia SILLA = new ProductoReferencia(
            "Silla Ergonómica Presidente New Dubai Black", "Sillas", null, "PEN", null, Map.of());

    @Test
    void decorplasUsaCatalogoYDescubreLaRutaCanonicaDelCasoControl() {
        var client = mock(SitioOficialClient.class);
        var extractor = mock(PrecioHtmlExtractor.class);
        var provider = new DecorplasProductoProvider(client, extractor);
        when(extractor.extraer(any())).thenReturn(new PrecioHtmlExtractor.Precio(
                new BigDecimal("2212.35"), "PEN", "SILLA ERGONOMICA PRESIDENTE NEW DUBAI BLACK"));
        String search = "https://decorplasonline.pe/catalogo/?search="
                + "silla%20ergonomica%20presidente%20new%20dubai%20black%20sillas";
        String product = "https://decorplasonline.pe/producto/silla-ergonomica-presidente-new-dubai-black";
        when(client.obtener(eq(search), anySet())).thenReturn(Jsoup.parse("<main></main>", search));
        when(client.obtener(eq(product), anySet())).thenReturn(Jsoup.parse(
                "<main><h1>SILLA ERGONOMICA PRESIDENTE NEW DUBAI BLACK</h1></main>", product));

        var candidates = provider.buscar(SILLA);

        assertEquals(1, candidates.size());
        assertEquals(product, candidates.getFirst().url());
        verify(client).obtener(eq(search), anySet());
    }

    @Test
    void pisopakUsaBusquedaPublicaYExtraeSoloEnlacesDeProducto() {
        var client = mock(SitioOficialClient.class);
        var extractor = mock(PrecioHtmlExtractor.class);
        var provider = new PisopakProductoProvider(client, extractor);
        when(extractor.extraer(any())).thenReturn(new PrecioHtmlExtractor.Precio(
                new BigDecimal("526.67"), "PEN", "ROPERO JASMIN CEDRO"));
        var reference = new ProductoReferencia("Ropero Jasmin Cedro", "Dormitorio", null, "PEN", null, Map.of());
        String search = "https://www.pisopak.com/?s=ropero+jasmin+cedro+dormitorio";
        String product = "https://www.pisopak.com/producto/ropero-jasmin-cedro/";
        when(client.obtener(eq(search), anySet())).thenReturn(Jsoup.parse(
                "<a href='" + product + "'>Ropero</a><a href='https://www.pisopak.com/productos/'>Categoría</a>", search));
        when(client.obtener(eq(product), anySet())).thenReturn(Jsoup.parse(
                "<main><h1>ROPERO JASMIN CEDRO</h1></main>", product));

        var candidates = provider.buscar(reference);

        assertEquals(1, candidates.size());
        assertEquals(product, candidates.getFirst().url());
        verify(client).obtener(eq(search), anySet());
    }
}
