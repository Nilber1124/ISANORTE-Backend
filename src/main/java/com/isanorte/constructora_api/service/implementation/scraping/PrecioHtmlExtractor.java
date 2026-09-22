package com.isanorte.constructora_api.service.implementation.scraping;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.regex.Pattern;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;
import com.isanorte.constructora_api.exception.ScrapingPrecioException;
import static com.isanorte.constructora_api.enums.EstadoComparacionPrecio.*;

/** Conservative extraction: ambiguous offers/ranges fail closed rather than inventing a price. */
@Component
public class PrecioHtmlExtractor {
    public record Precio(BigDecimal precio, String moneda, String nombre) {}
    private final JsonMapper json = JsonMapper.builder().build();
    private static final Pattern EXCLUDED = Pattern.compile(
            "(?i)(cuota|installment|mensual|monthly|env[ií]o|shipping|descuento|discount|"
            + "old.?price|price.?old|regular.?price|list.?price|original.?price|tachado|compare.?at|"
            + "precio.?anterior|precio.?normal|recommended|related|recomendado|relacionado)");
    private static final Pattern ISO = Pattern.compile(
            "(?i)\\b(PEN|USD|EUR|GBP|CLP|COP|MXN|ARS|BRL|CAD|AUD|JPY|CNY|CHF|BOB|UYU)\\b");

    public Precio extraer(Document document) {
        List<JsonNode> products = new ArrayList<>();
        List<JsonNode> offers = new ArrayList<>();
        Map<String, JsonNode> ids = new HashMap<>();
        for (Element script : document.select("script[type=application/ld+json]")) {
            try { collect(json.readTree(script.data()), products, offers, ids, 0); }
            catch (RuntimeException ignored) { /* Invalid JSON-LD: try the remaining evidence. */ }
        }
        List<Precio> candidates = new ArrayList<>();
        // A listing with several Products is not a direct product page.
        if (products.size() > 1) throw ambiguous();
        for (JsonNode product : products) {
            readOffers(product.get("offers"), text(product, "name"), candidates, ids, new HashSet<>(), 0);
        }
        if (products.isEmpty()) {
            for (JsonNode offer : offers) {
                JsonNode item = offer.get("itemOffered");
                if (item != null && type(item, "Product")) {
                    readOffers(offer, text(item, "name"), candidates, ids, new HashSet<>(), 0);
                }
            }
        }
        Precio price = unique(candidates);
        if (price != null) return price;

        String title = meta(document, "og:title");
        if (title == null) title = document.select("main h1").size() == 1
                ? document.selectFirst("main h1").text() : null;
        for (String key : List.of("product:sale_price:amount", "product:price:amount", "og:price:amount", "og:product:price:amount")) {
            String prefix = key.substring(0, key.lastIndexOf(':'));
            String currency = meta(document, prefix + ":currency");
            if (currency == null && key.equals("product:sale_price:amount")) {
                currency = meta(document, "product:price:currency");
            }
            for (Element element : document.select("meta[property=\"" + key + "\"],meta[name=\"" + key + "\"]")) {
                add(candidates, element.attr("content"), currency, title, true);
            }
            if (key.equals("product:sale_price:amount") && !candidates.isEmpty()) return unique(candidates);
        }
        price = unique(candidates);
        if (price != null) return price;

        var microProducts = document.select("[itemscope][itemtype$='/Product']");
        if (microProducts.size() > 1) throw ambiguous();
        for (Element product : microProducts) {
            Element name = product.selectFirst("[itemprop=name]");
            for (Element element : product.select("[itemprop=price]")) {
                if (excluded(element)) continue;
                Element scope = element.closest("[itemscope]");
                if (scope != null && scope != product && !scope.attr("itemtype").endsWith("/Offer")) continue;
                if (scope == null) scope = product;
                Element currency = scope.selectFirst("[itemprop=priceCurrency]");
                add(candidates, value(element), currency == null ? null : value(currency),
                        name == null ? title : value(name), element.hasAttr("content"));
            }
        }
        price = unique(candidates);
        if (price != null) return price;

        // Only current-price elements in a product context, never arbitrary numbers in page text.
        // Prefer an explicit sale price. A .price container with multiple amounts is rejected.
        for (String selector : List.of(
                "main .sale-price, main .special-price, main .price--sale, main ins .amount, main ins.price",
                "main [data-product-price], main .product-price, main .price-current, main .current-price,"
                        + " main .product .price, main .product-detail .price, main .product-info .price")) {
            for (Element element : document.select(selector)) {
                if (!excluded(element)) {
                    add(candidates, element.hasAttr("data-product-price") ? element.attr("data-product-price")
                            : value(element), element.attr("data-currency"), title, false);
                }
            }
            price = unique(candidates);
            if (price != null) return price;
        }
        throw new ScrapingPrecioException(PRICE_NOT_FOUND, "No fue posible obtener el precio de esta página.");
    }

    private void collect(JsonNode node, List<JsonNode> products, List<JsonNode> offers,
            Map<String, JsonNode> ids, int depth) {
        if (node == null || depth > 24) return;
        if (node.isObject()) {
            String id = text(node, "@id");
            if (id != null && node.size() > 1) ids.put(id, node);
            if (type(node, "Product")) products.add(node);
            if (type(node, "Offer")) offers.add(node);
        }
        if (node.isArray() || node.isObject()) {
            for (JsonNode child : node) collect(child, products, offers, ids, depth + 1);
        }
    }

    private void readOffers(JsonNode node, String name, List<Precio> results,
            Map<String, JsonNode> ids, Set<String> visited, int depth) {
        if (node == null || depth > 12) return;
        if (node.isArray()) {
            for (JsonNode child : node) readOffers(child, name, results, ids, new HashSet<>(visited), depth + 1);
            return;
        }
        String id = text(node, "@id");
        if (id != null && node.size() == 1 && ids.containsKey(id)) {
            if (!visited.add(id)) return;
            readOffers(ids.get(id), name, results, ids, visited, depth + 1);
            return;
        }
        if (!type(node, "Offer") && !type(node, "AggregateOffer")) return;
        if (type(node, "AggregateOffer")) {
            // lowPrice/highPrice are a range, not a confirmed price for the linked variant.
            readOffers(node.get("offers"), name, results, ids, visited, depth + 1);
            return;
        }
        String until = text(node, "priceValidUntil");
        if (until != null) {
            try { if (LocalDate.parse(until).isBefore(LocalDate.now())) return; }
            catch (java.time.format.DateTimeParseException ignored) { return; }
        }
        String amount = text(node, "price");
        String currency = text(node, "priceCurrency");
        if (amount != null) {
            add(results, amount, currency, name, true);
        } else {
            JsonNode specification = node.get("priceSpecification");
            if (specification != null && !specification.isArray()
                    && text(specification, "priceType") == null
                    && text(specification, "billingDuration") == null
                    && text(specification, "billingIncrement") == null
                    && text(specification, "unitCode") == null) {
                add(results, text(specification, "price"),
                        text(specification, "priceCurrency") == null ? currency
                                : text(specification, "priceCurrency"), name, true);
            }
        }
    }

    private static boolean type(JsonNode node, String expected) {
        JsonNode type = node.get("@type");
        if (type == null) return false;
        if (type.isArray()) {
            for (JsonNode item : type) if (schemaType(item.asText(), expected)) return true;
            return false;
        }
        return schemaType(type.asText(), expected);
    }

    private static boolean schemaType(String actual, String expected) {
        return actual.equals(expected) || actual.equals("https://schema.org/" + expected)
                || actual.equals("http://schema.org/" + expected);
    }

    private static String text(JsonNode node, String field) {
        JsonNode value = node == null ? null : node.get(field);
        return value == null || value.isNull() || !value.isValueNode() ? null : value.asText();
    }

    private static String meta(Document doc, String key) {
        Element element = doc.selectFirst("meta[property=\"" + key + "\"],meta[name=\"" + key + "\"]");
        return element == null ? null : element.attr("content");
    }

    private static String value(Element element) {
        return element.hasAttr("content") ? element.attr("content") : element.text();
    }

    private static boolean excluded(Element element) {
        for (Element current = element; current != null && !current.tagName().equals("main");
                current = current.parent()) {
            if (Set.of("del", "s", "strike", "nav", "footer", "aside").contains(current.tagName())
                    || current.hasAttr("hidden") || current.attr("aria-hidden").equals("true")
                    || EXCLUDED.matcher(current.className() + " " + current.id()).find()
                    || current.attr("style").matches("(?i).*(display\\s*:\\s*none|line-through).*")) return true;
        }
        return EXCLUDED.matcher(element.text()).find();
    }

    private static void add(List<Precio> prices, String raw, String explicitCurrency, String name, boolean machine) {
        BigDecimal amount = normalizarPrecio(raw, machine);
        if (amount == null) return;
        String currency = currency(explicitCurrency);
        String symbolCurrency = currency(raw);
        if (currency != null && symbolCurrency != null && !currency.equals(symbolCurrency)) throw ambiguous();
        prices.add(new Precio(amount, currency == null ? symbolCurrency : currency,
                name == null ? null : name.strip().substring(0, Math.min(name.strip().length(), 250))));
    }

    static String currency(String raw) {
        if (raw == null || raw.isBlank()) return null;
        var matcher = ISO.matcher(raw);
        Set<String> currencies = new HashSet<>();
        while (matcher.find()) currencies.add(matcher.group(1).toUpperCase(Locale.ROOT));
        if (Pattern.compile("(?i)S\\s*/\\.?|\\bsoles\\b").matcher(raw).find()) currencies.add("PEN");
        if (raw.contains("€")) currencies.add("EUR");
        if (raw.contains("£")) currencies.add("GBP");
        if (Pattern.compile("(?i)US\\s*\\$").matcher(raw).find()) currencies.add("USD");
        // Bare "$" is ambiguous (USD, CLP, COP, MXN, ...).
        return currencies.size() == 1 ? currencies.iterator().next() : null;
    }

    static BigDecimal normalizarPrecio(String raw, boolean machine) {
        if (raw == null || raw.length() > 100 || EXCLUDED.matcher(raw).find() || raw.contains("%")) return null;
        String value = raw.replace('\u00a0', ' ').trim()
                .replaceAll("(?i)\\b(PEN|USD|EUR|GBP|CLP|COP|MXN|ARS|BRL|CAD|AUD|JPY|CNY|CHF|BOB|UYU|soles)\\b", "")
                .replaceAll("(?i)US\\s*\\$|S\\s*/\\.?", "").replaceAll("[€£$]", "").trim();
        // Space-separated amounts or ranges are not a single price.
        if (!value.matches("\\d[\\d.,]*")) return null;
        if (value.contains(".") && value.contains(",")) {
            char decimal = value.lastIndexOf('.') > value.lastIndexOf(',') ? '.' : ',';
            String grouping = decimal == '.' ? "," : ".";
            String pattern = "\\d{1,3}(?:" + Pattern.quote(grouping) + "\\d{3})+"
                    + Pattern.quote(String.valueOf(decimal)) + "\\d{1,2}";
            if (!value.matches(pattern)) return null;
            value = value.replace(grouping, "").replace(decimal, '.');
        } else if (value.contains(",") || value.contains(".")) {
            char separator = value.contains(",") ? ',' : '.';
            int digits = value.length() - value.lastIndexOf(separator) - 1;
            long count = value.chars().filter(ch -> ch == separator).count();
            if (count == 1 && digits >= 1 && digits <= 2) {
                value = value.replace(separator, '.');
            } else if (!machine && value.matches("\\d{1,3}(?:" + Pattern.quote("" + separator) + "\\d{3})+")) {
                value = value.replace("" + separator, "");
            } else return null; // Never round an ambiguous decimal/grouping representation.
        }
        try {
            BigDecimal result = new BigDecimal(value).setScale(2, RoundingMode.UNNECESSARY);
            return result.precision() <= 12 && result.signum() >= 0 ? result : null;
        } catch (ArithmeticException | NumberFormatException ex) { return null; }
    }

    private static Precio unique(List<Precio> prices) {
        if (prices.isEmpty()) return null;
        Precio first = prices.getFirst();
        if (prices.stream().anyMatch(p -> p.precio().compareTo(first.precio()) != 0
                || !Objects.equals(p.moneda(), first.moneda()))) throw ambiguous();
        return first;
    }

    private static ScrapingPrecioException ambiguous() {
        return new ScrapingPrecioException(NOT_COMPARABLE,
                "La página contiene productos, precios o monedas ambiguos; no es posible comparar con seguridad.");
    }
}
