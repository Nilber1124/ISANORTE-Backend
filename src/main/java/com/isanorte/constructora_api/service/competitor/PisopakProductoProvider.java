package com.isanorte.constructora_api.service.competitor;

import java.util.Set;
import org.springframework.stereotype.Component;
import com.isanorte.constructora_api.service.implementation.scraping.PrecioHtmlExtractor;
import com.isanorte.constructora_api.service.implementation.scraping.SitioOficialClient;

@Component
public class PisopakProductoProvider extends AbstractHtmlCompetidorProvider {
    private static final Set<String> DOMINIOS = Set.of("www.pisopak.com", "pisopak.com");
    public PisopakProductoProvider(SitioOficialClient client, PrecioHtmlExtractor extractor) { super(client, extractor); }
    public String empresa() { return "PISOPAK"; }
    protected Set<String> dominios() { return DOMINIOS; }
    protected String urlBusqueda(String q) { return "https://www.pisopak.com/?s=" + q; }
    protected String selectorEnlaces() { return "a[href*='/producto/']"; }
    protected boolean esUrlProducto(String url) {
        return !url.isBlank() && url.contains("/producto/")
                && !url.contains("/wp-content/") && !url.contains("/carrito/") && !url.contains("?s=");
    }
}
