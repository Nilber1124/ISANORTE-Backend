package com.isanorte.constructora_api.service.competitor;

import java.util.Set;
import java.util.List;
import org.springframework.stereotype.Component;
import com.isanorte.constructora_api.service.implementation.scraping.PrecioHtmlExtractor;
import com.isanorte.constructora_api.service.implementation.scraping.SitioOficialClient;

@Component
public class DecorplasProductoProvider extends AbstractHtmlCompetidorProvider {

    private static final Set<String> DOMINIOS = Set.of(
            "decorplasonline.pe",
            "www.decorplasonline.pe"
    );

    public DecorplasProductoProvider(
            SitioOficialClient client,
            PrecioHtmlExtractor extractor
    ) {
        super(client, extractor);
    }

    public String empresa() { return "DECORPLAS"; }

    protected Set<String> dominios() { return DOMINIOS; }

    protected String urlBusqueda(String q) {
        return "https://decorplasonline.pe/catalogo/?search=" + q.replace("+", "%20");
    }
    protected String selectorEnlaces() {
        return "a[href*='/producto/']";
    }

    protected boolean esUrlProducto(String url) {
        return !url.isBlank()
                && url.contains("/producto/")
                && !url.contains("/category/")
                && !url.contains("/author/")
                && !url.contains("/wp-content/")
                && !url.contains("/carrito/")
                && !url.contains("?search=");
    }

    /** El catálogo renderiza resultados con JavaScript; se prueba como máximo la ruta canónica derivada. */
    @Override
    protected List<String> urlsDirectas(ProductoReferencia referencia) {
        String slug = normalizar(referencia.nombre()).replace(' ', '-');
        return slug.isBlank() ? List.of() : List.of("https://decorplasonline.pe/producto/" + slug);
    }
}
