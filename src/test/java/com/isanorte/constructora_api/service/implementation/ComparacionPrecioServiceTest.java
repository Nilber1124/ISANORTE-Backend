package com.isanorte.constructora_api.service.implementation;

import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import com.isanorte.constructora_api.dto.request.ComparacionPrecioRequest;
import com.isanorte.constructora_api.dto.response.PublicProductDetailResponse;
import com.isanorte.constructora_api.exception.*;
import com.isanorte.constructora_api.service.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static com.isanorte.constructora_api.enums.EstadoComparacionPrecio.*;

class ComparacionPrecioServiceTest {
    private IPublicContentService content;
    private IScrapingPrecioService scraping;
    private ComparacionPrecioService service;
    private final ComparacionPrecioRequest request = new ComparacionPrecioRequest("https://tienda.example/mesa");

    @BeforeEach void setup() {
        content = mock(IPublicContentService.class);
        scraping = mock(IScrapingPrecioService.class);
        service = new ComparacionPrecioService(content, scraping);
        internal("850.00");
        external("920.00", "PEN");
    }

    void internal(String price) {
        when(content.findPublicProduct("isanorte", "isadecor", "mesa")).thenReturn(
                new PublicProductDetailResponse("Mesa", "SKU", "mesa", null, "Mesa", null, null,
                        price == null ? null : new BigDecimal(price), null, null, null,
                        false, null, null, null, null, null, null));
    }
    void external(String price, String currency) {
        when(scraping.extraer(request.urlExterna())).thenReturn(new IScrapingPrecioService.PrecioExterno(
                request.urlExterna(), "tienda.example", "Mesa externa", new BigDecimal(price), currency));
    }

    @ParameterizedTest
    @CsvSource({"920.00,70.00,8.24,ISADECOR", "800.00,-50.00,-5.88,externo", "850.00,0.00,0.00,iguales"})
    void comparaConBigDecimal(String price, String difference, String percentage, String message) {
        external(price, "PEN");
        var result = service.comparar("isanorte", "isadecor", "mesa", request);
        assertEquals(SUCCESS, result.estado());
        assertTrue(result.comparable());
        assertEquals(new BigDecimal(difference), result.diferencia());
        assertEquals(new BigDecimal(percentage), result.porcentajeDiferencia());
        assertTrue(result.mensaje().contains(message));
        verify(content).findPublicProduct("isanorte", "isadecor", "mesa");
    }

    @Test void noComparaMonedasDiferentes() {
        external("920", "USD");
        var result = service.comparar("isanorte", "isadecor", "mesa", request);
        assertEquals(CURRENCY_MISMATCH, result.estado());
        assertFalse(result.comparable());
        assertNull(result.diferencia());
        assertNull(result.porcentajeDiferencia());
    }

    @Test void noAsumeMonedaExterna() {
        external("920", null);
        assertEquals(CURRENCY_UNKNOWN, service.comparar("isanorte", "isadecor", "mesa", request).estado());
    }

    @Test void precioInternoNuloNoEsComparable() {
        internal(null);
        var result = service.comparar("isanorte", "isadecor", "mesa", request);
        assertEquals(NOT_COMPARABLE, result.estado());
        assertNull(result.diferencia());
    }

    @Test void ceroNoDividePorCero() {
        internal("0.00");
        var result = service.comparar("isanorte", "isadecor", "mesa", request);
        assertTrue(result.comparable());
        assertNull(result.porcentajeDiferencia());
    }

    @Test void erroresExternosSonResultadosControlados() {
        when(scraping.extraer(any())).thenThrow(new ScrapingPrecioException(PRICE_NOT_FOUND, "Sin precio"));
        var result = service.comparar("isanorte", "isadecor", "mesa", request);
        assertEquals(PRICE_NOT_FOUND, result.estado());
        assertNull(result.urlExterna());
        assertFalse(result.comparable());
    }

    @Test void productoFueraDeUnidadNoIniciaScraping() {
        when(content.findPublicProduct(any(), any(), any())).thenThrow(new ModelNotFoundException("No publicado"));
        assertThrows(ModelNotFoundException.class, () -> service.comparar("isanorte", "otra", "mesa", request));
        verifyNoInteractions(scraping);
    }
}

