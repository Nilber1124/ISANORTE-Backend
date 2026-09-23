package com.isanorte.constructora_api.service.competitor;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import com.isanorte.constructora_api.exception.ScrapingPrecioException;
import com.isanorte.constructora_api.service.implementation.scraping.PrecioHtmlExtractor;
import com.isanorte.constructora_api.service.implementation.scraping.SitioOficialClient;

public abstract class AbstractHtmlCompetidorProvider implements CompetidorProductoProvider {
    private static final int MAX_CANDIDATOS = 6;
    private static final Pattern UNIDAD = Pattern.compile("(?i)(?:/|\\bpor\\s+)(m[²2]|caja|unidad|und|c/u|ml)\\b");
    protected final SitioOficialClient client;
    protected final PrecioHtmlExtractor precioExtractor;

    protected AbstractHtmlCompetidorProvider(SitioOficialClient client, PrecioHtmlExtractor precioExtractor) {
        this.client = client;
        this.precioExtractor = precioExtractor;
    }

    protected abstract Set<String> dominios();
    protected abstract String urlBusqueda(String consultaCodificada);
    protected abstract String selectorEnlaces();
    protected abstract boolean esUrlProducto(String url);
    protected List<String> urlsDirectas(ProductoReferencia referencia) { return List.of(); }

    @Override
    public List<ProductoCompetidor> buscar(ProductoReferencia referencia) {
        String query = construirConsulta(referencia);
        Document resultados = client.obtener(urlBusqueda(URLEncoder.encode(query, StandardCharsets.UTF_8)), dominios());
        LinkedHashSet<String> urls = new LinkedHashSet<>();
        for (Element enlace : resultados.select(selectorEnlaces())) {
            String url = enlace.absUrl("href");
            if (esUrlProducto(url) && dominios().contains(host(url))) urls.add(url);
            if (urls.size() == MAX_CANDIDATOS) break;
        }
        for (String url : urlsDirectas(referencia)) {
            if (urls.size() == MAX_CANDIDATOS) break;
            if (esUrlProducto(url) && dominios().contains(host(url))) urls.add(url);
        }
        List<ProductoCompetidor> candidatos = new ArrayList<>();
        for (String url : urls) {
            try { candidatos.add(extraer(client.obtener(url, dominios()), url)); }
            catch (ScrapingPrecioException ignored) { /* Un candidato defectuoso no invalida la tienda. */ }
        }
        return candidatos;
    }

    private ProductoCompetidor extraer(Document doc, String url) {
        String nombre = contenidoMeta(doc, "og:title");
        if (nombre == null) {
            Element h1 = doc.selectFirst("main h1, h1.product_title, article h1, h1");
            nombre = h1 == null ? doc.title() : h1.text();
        }
        Map<String, String> caracteristicas = new LinkedHashMap<>();
        for (Element row : doc.select("table tr")) {
            var cells = row.select("th,td");
            if (cells.size() >= 2) agregar(caracteristicas, cells.get(0).text(), cells.get(1).text());
        }
        for (Element dt : doc.select("dl dt")) {
            Element dd = dt.nextElementSibling();
            if (dd != null && dd.tagName().equals("dd")) agregar(caracteristicas, dt.text(), dd.text());
        }
        BigDecimal precio = null;
        String moneda = null;
        try {
            var extraido = precioExtractor.extraer(doc);
            precio = extraido.precio(); moneda = extraido.moneda();
            if (extraido.nombre() != null && !extraido.nombre().isBlank()) nombre = extraido.nombre();
        } catch (ScrapingPrecioException ignored) { /* La similitud también puede evaluarse sin precio. */ }
        String textPrecio = Optional.ofNullable(doc.selectFirst("main .price, main [itemprop=price], .product .price"))
                .map(Element::text).orElse("");
        Matcher unit = UNIDAD.matcher(textPrecio);
        String unidad = unit.find() ? normalizarUnidad(unit.group(1)) : null;
        return new ProductoCompetidor(empresa(), limpiarTitulo(nombre), url, precio, null,
                normalizarMoneda(moneda, textPrecio), unidad, null, caracteristicas, BigDecimal.ZERO);
    }

    private static String construirConsulta(ProductoReferencia ref) {
        LinkedHashSet<String> terms = new LinkedHashSet<>();
        agregarTokens(terms, ref.nombre());
        agregarTokens(terms, ref.categoria());
        ref.caracteristicas().forEach((k, v) -> {
            String key = normalizar(k);
            if (key.contains("material") || key.contains("modelo") || key.contains("color")
                    || key.contains("espesor") || key.contains("dimension")) agregarTokens(terms, v);
        });
        return String.join(" ", terms.stream().limit(9).toList());
    }

    private static void agregarTokens(Set<String> terms, String value) {
        if (value == null) return;
        for (String token : normalizar(value).split(" ")) if (token.length() > 1) terms.add(token);
    }
    private static String contenidoMeta(Document doc, String property) {
        Element e = doc.selectFirst("meta[property='" + property + "'],meta[name='" + property + "']");
        return e == null || e.attr("content").isBlank() ? null : e.attr("content").trim();
    }
    private static void agregar(Map<String, String> map, String key, String value) {
        if (!key.isBlank() && !value.isBlank() && key.length() <= 80 && value.length() <= 300)
            map.putIfAbsent(key.trim(), value.trim());
    }
    private static String host(String value) {
        try { return java.net.URI.create(value).getHost().toLowerCase(Locale.ROOT); }
        catch (RuntimeException ex) { return ""; }
    }
    private static String limpiarTitulo(String value) {
        return value == null ? "Producto sin nombre" : value.replaceAll("\\s*[|–-]\\s*(Pisopak|Decorplas).*$", "").trim();
    }
    private static String normalizarMoneda(String value, String text) {
        if (value != null && !value.isBlank()) return value.equalsIgnoreCase("S/") ? "PEN" : value.toUpperCase(Locale.ROOT);
        return text.matches("(?s).*(S/|S\\.).*") ? "PEN" : null;
    }
    private static String normalizarUnidad(String value) {
        String n = normalizar(value);
        if (n.equals("m2")) return "m2";
        if (n.equals("caja")) return "caja";
        if (n.equals("ml")) return "ml";
        return "unidad";
    }
    protected static String normalizar(String value) {
        if (value == null) return "";
        return Normalizer.normalize(value, Normalizer.Form.NFD).replaceAll("\\p{M}", "")
                .toLowerCase(Locale.ROOT).replace('²', '2').replaceAll("[^a-z0-9]+", " ").trim();
    }
}
