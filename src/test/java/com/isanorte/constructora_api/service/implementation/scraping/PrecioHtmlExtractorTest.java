package com.isanorte.constructora_api.service.implementation.scraping;

import org.jsoup.Jsoup;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import com.isanorte.constructora_api.exception.ScrapingPrecioException;
import static org.junit.jupiter.api.Assertions.*;
import static com.isanorte.constructora_api.enums.EstadoComparacionPrecio.*;

class PrecioHtmlExtractorTest {
    private final PrecioHtmlExtractor extractor = new PrecioHtmlExtractor();

    @Test void extraeJsonLdYLoPrioriza() {
        var result = extractor.extraer(Jsoup.parse("""
            <script type="application/ld+json">{"@context":"https://schema.org","@type":"Product",
            "name":"Mesa","offers":{"@type":"Offer","price":"1299.90","priceCurrency":"PEN"}}</script>
            <meta property="product:price:amount" content="1400">
            <meta property="product:price:currency" content="USD">
            """));
        assertEquals("1299.90", result.precio().toPlainString());
        assertEquals("PEN", result.moneda());
        assertEquals("Mesa", result.nombre());
    }

    @Test void resuelveGraphYReferenciaOffer() {
        var result = extractor.extraer(Jsoup.parse("""
            <script type="application/ld+json">{"@graph":[
            {"@type":"Product","name":"Mesa","offers":{"@id":"#oferta"}},
            {"@id":"#oferta","@type":"Offer","price":920,"priceCurrency":"PEN"}]}</script>
            """));
        assertEquals("920.00", result.precio().toPlainString());
    }

    @Test void extraeMetadata() {
        var result = extractor.extraer(Jsoup.parse("""
            <meta property="product:price:amount" content="1.299,90">
            <meta property="product:price:currency" content="PEN">
            <meta property="og:title" content="Mesa moderna">
            """));
        assertEquals("1299.90", result.precio().toPlainString());
        assertEquals("Mesa moderna", result.nombre());
    }

    @Test void extraeMicrodatos() {
        var result = extractor.extraer(Jsoup.parse("""
            <div itemscope itemtype="https://schema.org/Product"><span itemprop="name">Mesa</span>
            <div itemscope itemtype="https://schema.org/Offer">
            <meta itemprop="price" content="850.00"><meta itemprop="priceCurrency" content="PEN">
            </div></div>
            """));
        assertEquals("850.00", result.precio().toPlainString());
        assertEquals("PEN", result.moneda());
    }

    @Test void metadataPromocionalTienePrioridadSobrePrecioNormal() {
        var result = extractor.extraer(Jsoup.parse("""
            <meta property="product:price:amount" content="1000">
            <meta property="product:sale_price:amount" content="850">
            <meta property="product:price:currency" content="PEN">
            """));
        assertEquals("850.00", result.precio().toPlainString());
        assertEquals("PEN", result.moneda());
    }

    @Test void precioDeEnvioEnMicrodatosNoEsPrecioDeProducto() {
        assertEquals(PRICE_NOT_FOUND, assertThrows(ScrapingPrecioException.class,
                () -> extractor.extraer(Jsoup.parse("""
                <div itemscope itemtype="https://schema.org/Product">
                  <div itemscope itemtype="https://schema.org/DeliveryChargeSpecification">
                    <meta itemprop="price" content="20">
                    <meta itemprop="priceCurrency" content="PEN">
                  </div>
                </div>
                """))).getEstado());
    }

    @ParameterizedTest
    @ValueSource(strings = {"S/ 1,299.90", "S/. 1,299.90", "PEN 1299.90", "S/ 1.299,90"})
    void normalizaPrecioVisual(String value) {
        var result = extractor.extraer(Jsoup.parse("<main><h1>Mesa</h1><div class='product-price'>" + value + "</div></main>"));
        assertEquals("1299.90", result.precio().toPlainString());
        assertEquals("PEN", result.moneda());
    }

    @Test void prefiereOfertaVisualYDescartaPrecioTachado() {
        var result = extractor.extraer(Jsoup.parse("""
            <main><div class="product"><del class="price">S/ 1000</del>
            <span class="sale-price">S/ 850</span><span class="installment price">S/ 50</span></div></main>
            """));
        assertEquals("850.00", result.precio().toPlainString());
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "<main><h1>Mesa SKU 129990</h1><p>Teléfono 999999999</p></main>",
        "<main><div class='product'><span class='price'>12 cuotas de S/ 50</span></div></main>",
        "<main><div class='product'><span class='price'>20% descuento</span></div></main>",
        "<main><div class='product'><del class='price'>S/ 900</del></div></main>",
        "<main><div class='shipping product-price'>S/ 20</div></main>",
        "<main><div class='product-price'>S/ 20 S/ 30</div></main>"
    })
    void noConfundeNumerosConPrecio(String html) {
        assertEquals(PRICE_NOT_FOUND, assertThrows(ScrapingPrecioException.class,
                () -> extractor.extraer(Jsoup.parse(html))).getEstado());
    }

    @Test void dolarSinPaisNoDeterminaMoneda() {
        var result = extractor.extraer(Jsoup.parse("<main><div class='product-price'>$ 50.00</div></main>"));
        assertNull(result.moneda());
    }

    @Test void variasOfertasDiferentesNoSonComparables() {
        assertEquals(NOT_COMPARABLE, assertThrows(ScrapingPrecioException.class, () -> extractor.extraer(Jsoup.parse("""
            <script type="application/ld+json">{"@type":"Product","offers":[
            {"@type":"Offer","price":"50","priceCurrency":"PEN"},
            {"@type":"Offer","price":"80","priceCurrency":"PEN"}]}</script>
            """))).getEstado());
    }
}
