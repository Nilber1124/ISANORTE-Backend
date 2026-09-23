package com.isanorte.constructora_api.service.implementation;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.OffsetDateTime;
import java.util.*;
import org.springframework.stereotype.Service;
import com.isanorte.constructora_api.dto.response.ComparacionCompetidoresResponse;
import com.isanorte.constructora_api.dto.response.ComparacionCompetidoresResponse.*;
import com.isanorte.constructora_api.dto.response.PublicProductDetailResponse;
import com.isanorte.constructora_api.enums.EstadoComparacionCompetidor;
import com.isanorte.constructora_api.exception.ScrapingPrecioException;
import com.isanorte.constructora_api.service.IComparacionCompetidoresService;
import com.isanorte.constructora_api.service.IPublicContentService;
import com.isanorte.constructora_api.service.competitor.*;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ComparacionCompetidoresService implements IComparacionCompetidoresService {
    private static final String MONEDA_ISADECOR = "PEN";
    private final IPublicContentService publicContentService;
    private final List<CompetidorProductoProvider> providers;
    private final SimilitudProductoService similitud;

    public ComparacionCompetidoresResponse comparar(String clave, String unidadSlug, String productoSlug) {
        PublicProductDetailResponse product = publicContentService.findPublicProduct(clave, unidadSlug, productoSlug);
        LinkedHashMap<String, String> specs = new LinkedHashMap<>();
        product.especificaciones().forEach(s -> specs.put(s.clave(), s.valor()));
        String categoria = product.categorias().stream().map(c -> c.nombre()).reduce((a, b) -> a + " " + b).orElse("");
        String unidad = product.configuracionCalculo() == null ? null : normalizarUnidad(product.configuracionCalculo().unidadVenta());
        var reference = new ProductoReferencia(product.nombre(), categoria, product.precioBase(), MONEDA_ISADECOR, unidad, specs);
        List<ResultadoCompetidor> results = providers.stream()
                .sorted(Comparator.comparing(CompetidorProductoProvider::empresa)).map(p -> consultar(p, reference)).toList();
        var interno = new ProductoComparable(product.nombre(), product.precioBase(), product.precioAnterior(),
                MONEDA_ISADECOR, unidad, caracteristicas(specs));
        return new ComparacionCompetidoresResponse(interno, results, diferencias(interno, results), OffsetDateTime.now());
    }

    private ResultadoCompetidor consultar(CompetidorProductoProvider provider, ProductoReferencia reference) {
        try {
            Optional<ProductoCompetidor> best = provider.buscar(reference).stream()
                    .map(c -> c.conSimilitud(similitud.calcular(reference, c)))
                    .filter(c -> c.similitud().compareTo(SimilitudProductoService.UMBRAL) >= 0)
                    .max(Comparator.comparing(ProductoCompetidor::similitud));
            if (best.isEmpty()) return vacio(provider.empresa(), EstadoComparacionCompetidor.NO_SIMILAR_PRODUCT_FOUND,
                    "No se encontró un producto suficientemente similar en " + provider.empresa() + ".");
            ProductoCompetidor c = best.get();
            boolean comparable = reference.precio() != null && c.precio() != null
                    && reference.moneda().equalsIgnoreCase(c.moneda())
                    && reference.unidadPrecio() != null && reference.unidadPrecio().equals(c.unidadPrecio());
            BigDecimal diferencia = comparable ? c.precio().subtract(reference.precio()).setScale(2, RoundingMode.HALF_UP) : null;
            return new ResultadoCompetidor(c.empresa(), EstadoComparacionCompetidor.FOUND, true, c.nombre(), c.url(),
                    c.precio(), c.precioAnterior(), c.moneda(), c.unidadPrecio(), c.cantidadPorPresentacion(),
                    c.similitud(), caracteristicas(c.caracteristicas()), comparable, diferencia,
                    comparable ? "Precios expresados en la misma moneda y unidad."
                            : "Los precios no se comparan porque falta la unidad o no coincide con ISADECOR.");
        } catch (RuntimeException ex) {
            return vacio(provider.empresa(), EstadoComparacionCompetidor.STORE_UNAVAILABLE,
                    "No fue posible consultar esta tienda.");
        }
    }

    private static ResultadoCompetidor vacio(String empresa, EstadoComparacionCompetidor estado, String mensaje) {
        return new ResultadoCompetidor(empresa, estado, false, null, null, null, null, null, null,
                null, null, List.of(), false, null, mensaje);
    }
    private static List<Caracteristica> caracteristicas(Map<String, String> map) {
        return map.entrySet().stream().map(e -> new Caracteristica(e.getKey(), e.getValue())).toList();
    }
    private static List<String> diferencias(ProductoComparable internal, List<ResultadoCompetidor> results) {
        Map<String, String> own = new HashMap<>();
        internal.caracteristicas().forEach(c -> own.put(SimilitudProductoService.clave(c.nombre()), c.valor()));
        List<String> differences = new ArrayList<>();
        for (ResultadoCompetidor result : results) if (result.encontrado()) {
            for (Caracteristica item : result.caracteristicas()) {
                String value = own.get(SimilitudProductoService.clave(item.nombre()));
                if (value != null && !value.equalsIgnoreCase(item.valor()) && differences.size() < 6)
                    differences.add("ISADECOR publica " + item.nombre() + ": " + value + "; "
                            + result.empresa() + " publica " + item.valor() + ".");
            }
        }
        return differences;
    }
    private static String normalizarUnidad(String value) {
        if (value == null || value.isBlank()) return null;
        String n = java.text.Normalizer.normalize(value, java.text.Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "").toLowerCase(Locale.ROOT).replace("²", "2").trim();
        if (n.matches("m2|m\\^2|metro cuadrado|metros cuadrados")) return "m2";
        if (n.matches("caja|cajas")) return "caja";
        if (n.matches("ml|metro lineal|metros lineales")) return "ml";
        if (n.matches("unidad|unidades|und|c/u")) return "unidad";
        return null;
    }
}
