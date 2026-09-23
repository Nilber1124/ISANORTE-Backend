package com.isanorte.constructora_api.service.competitor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.Normalizer;
import java.util.*;
import org.springframework.stereotype.Component;

@Component
public class SimilitudProductoService {
    public static final BigDecimal UMBRAL = new BigDecimal("0.45");
    private static final BigDecimal PESO_NOMBRE = new BigDecimal("0.55");
    private static final BigDecimal PESO_CATEGORIA = new BigDecimal("0.20");
    private static final BigDecimal PESO_CARACTERISTICAS = new BigDecimal("0.25");
    private static final Map<String, String> ALIASES = Map.of(
            "grosor", "espesor", "capa de desgaste", "capa de uso", "medidas", "dimensiones");

    public BigDecimal calcular(ProductoReferencia ref, ProductoCompetidor candidato) {
        BigDecimal nombre = jaccard(tokens(ref.nombre()), tokens(candidato.nombre()));
        BigDecimal categoria = jaccard(tokens(ref.categoria()), tokens(candidato.nombre()));
        BigDecimal specs = similitudCaracteristicas(ref.caracteristicas(), candidato.caracteristicas());
        return nombre.multiply(PESO_NOMBRE).add(categoria.multiply(PESO_CATEGORIA))
                .add(specs.multiply(PESO_CARACTERISTICAS)).setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal similitudCaracteristicas(Map<String, String> left, Map<String, String> right) {
        Map<String, String> normalizedRight = new HashMap<>();
        right.forEach((k, v) -> normalizedRight.put(clave(k), v));
        BigDecimal sum = BigDecimal.ZERO; int count = 0;
        for (var entry : left.entrySet()) {
            String other = normalizedRight.get(clave(entry.getKey()));
            if (other != null) { sum = sum.add(jaccard(tokens(entry.getValue()), tokens(other))); count++; }
        }
        return count == 0 ? BigDecimal.ZERO : sum.divide(BigDecimal.valueOf(count), 4, RoundingMode.HALF_UP);
    }
    private static BigDecimal jaccard(Set<String> a, Set<String> b) {
        if (a.isEmpty() || b.isEmpty()) return BigDecimal.ZERO;
        Set<String> intersection = new HashSet<>(a); intersection.retainAll(b);
        Set<String> union = new HashSet<>(a); union.addAll(b);
        return BigDecimal.valueOf(intersection.size()).divide(BigDecimal.valueOf(union.size()), 4, RoundingMode.HALF_UP);
    }
    public static String clave(String value) {
        String n = normalizar(value);
        return ALIASES.getOrDefault(n, n);
    }
    private static Set<String> tokens(String value) {
        Set<String> result = new HashSet<>();
        for (String token : normalizar(value).split(" ")) if (token.length() > 1) result.add(token);
        return result;
    }
    private static String normalizar(String value) {
        if (value == null) return "";
        return Normalizer.normalize(value, Normalizer.Form.NFD).replaceAll("\\p{M}", "")
                .toLowerCase(Locale.ROOT).replace('²', '2').replaceAll("[^a-z0-9]+", " ").trim();
    }
}
