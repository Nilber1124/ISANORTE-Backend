package com.isanorte.constructora_api.repository.specification;

import java.math.BigDecimal;
import java.text.Normalizer;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import org.springframework.data.jpa.domain.Specification;

import com.isanorte.constructora_api.enums.EstadoDisponibilidad;
import com.isanorte.constructora_api.enums.EstadoPublicacion;
import com.isanorte.constructora_api.enums.OrdenCatalogoProducto;
import com.isanorte.constructora_api.enums.ModalidadVenta;
import com.isanorte.constructora_api.model.CategoriaProducto;
import com.isanorte.constructora_api.model.Producto;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;

/**
 * Predicados dinámicos para el catálogo público de productos. Deliberadamente no hacen
 * fetch-join de colecciones (imagenes/categorias) para no activar la paginación en memoria
 * de Hibernate; la hidratación de esas colecciones se hace aparte, por página, en el service.
 */
public final class ProductoCatalogSpecifications {
    private static final String FIELD_BUSINESS_UNIT = "unidadNegocio";
    private static final String FIELD_NAME = "nombre";

    private ProductoCatalogSpecifications() {
    }

    public static Specification<Producto> perteneceAUnidad(UUID unidadId) {
        return (root, query, cb) -> cb.equal(root.get(FIELD_BUSINESS_UNIT).get("id"), unidadId);
    }

    public static Specification<Producto> estado(EstadoPublicacion estado) {
        return (root, query, cb) -> cb.equal(root.get("estado"), estado);
    }

    /** Máximo de valores aceptados en un filtro de lista, para acotar la consulta. */
    private static final int MAX_FILTER_VALUES = 12;

    /** Separa una lista «a,b,c» en valores sin espacios ni vacíos (máx. {@value #MAX_FILTER_VALUES}). */
    public static List<String> splitValues(String csv) {
        if (csv == null || csv.isBlank()) {
            return List.of();
        }
        return Arrays.stream(csv.split(",")).map(String::strip).filter(v -> !v.isEmpty())
                .distinct().limit(MAX_FILTER_VALUES).toList();
    }

    /** Productos de cualquiera de las colecciones indicadas (slugs separados por coma). */
    public static Specification<Producto> categoriaSlug(String slugs, UUID unidadId) {
        List<String> values = splitValues(slugs);
        if (values.isEmpty()) {
            return (root, query, cb) -> cb.conjunction();
        }
        return (root, query, cb) -> {
            Subquery<UUID> subquery = query.subquery(UUID.class);
            Root<Producto> correlatedRoot = subquery.correlate(root);
            Join<Producto, CategoriaProducto> categoria = correlatedRoot.join("categorias");
            subquery.select(correlatedRoot.get("id"));
            subquery.where(cb.and(
                    categoria.get("slug").in(values),
                    cb.isTrue(categoria.get("activo")),
                    cb.or(cb.isNull(categoria.get(FIELD_BUSINESS_UNIT)),
                            cb.equal(categoria.get(FIELD_BUSINESS_UNIT).get("id"), unidadId))));
            return cb.exists(subquery);
        };
    }

    private static final String ACCENTED = "áàäâãéèëêíìïîóòöôõúùüûñç";
    private static final String PLAIN = "aaaaaeeeeiiiiooooouuuunc";
    private static final int MAX_SEARCH_TERMS = 6;

    /**
     * Normaliza un texto para buscar: minúsculas y sin tildes («Sándwich» → «sandwich»).
     * Debe coincidir con {@link #sinTildes(CriteriaBuilder, Expression)}, que hace lo mismo en la base de datos.
     */
    public static String normalizarTexto(String text) {
        if (text == null) {
            return "";
        }
        return Normalizer.normalize(text, Normalizer.Form.NFD).replaceAll("\\p{M}", "").toLowerCase(Locale.ROOT).strip();
    }

    /**
     * Palabras de una búsqueda: normalizadas, sin repetidos y en singular («paneles» → «panel»,
     * «pisos» → «piso»), para que escribir el plural o sin tildes encuentre el producto.
     */
    public static List<String> terminosDeBusqueda(String texto) {
        return Arrays.stream(normalizarTexto(texto).split("[^a-z0-9]+"))
                .filter(term -> !term.isEmpty())
                .map(ProductoCatalogSpecifications::singular)
                .distinct().limit(MAX_SEARCH_TERMS).toList();
    }

    private static String singular(String term) {
        if (term.length() >= 6 && term.endsWith("es")) {
            return term.substring(0, term.length() - 2);
        }
        if (term.length() >= 4 && term.endsWith("s")) {
            return term.substring(0, term.length() - 1);
        }
        return term;
    }

    private static Expression<String> sinTildes(CriteriaBuilder cb, Expression<String> value) {
        return cb.function("translate", String.class, cb.lower(value), cb.literal(ACCENTED), cb.literal(PLAIN));
    }

    /**
     * Cada palabra debe aparecer (sin importar tildes, mayúsculas ni plural) en el nombre, SKU, resumen,
     * descripción, alguna colección o algún acabado del producto.
     */
    public static Specification<Producto> textoBusqueda(String termino) {
        List<String> terms = terminosDeBusqueda(termino);
        if (terms.isEmpty()) {
            return (root, query, cb) -> cb.conjunction();
        }
        return (root, query, cb) -> cb.and(terms.stream().map(term -> {
            String pattern = "%" + term.replace("!", "!!").replace("%", "!%").replace("_", "!_") + "%";
            Predicate inFields = cb.or(
                    cb.like(sinTildes(cb, root.get(FIELD_NAME)), pattern, '!'),
                    cb.like(sinTildes(cb, root.get("sku")), pattern, '!'),
                    cb.like(sinTildes(cb, cb.coalesce(root.<String>get("resumen"), "")), pattern, '!'),
                    cb.like(sinTildes(cb, root.get("descripcion")), pattern, '!'));

            Subquery<UUID> categories = query.subquery(UUID.class);
            Root<Producto> categoryRoot = categories.correlate(root);
            Join<Producto, CategoriaProducto> category = categoryRoot.join("categorias");
            categories.select(categoryRoot.get("id"));
            categories.where(cb.like(sinTildes(cb, category.get(FIELD_NAME)), pattern, '!'));

            Subquery<UUID> finishes = query.subquery(UUID.class);
            Root<Producto> finishRoot = finishes.correlate(root);
            var finish = finishRoot.join("variantes");
            finishes.select(finishRoot.get("id"));
            finishes.where(cb.like(sinTildes(cb, finish.<String>get(FIELD_NAME)), pattern, '!'));

            return cb.or(inFields, cb.exists(categories), cb.exists(finishes));
        }).toArray(Predicate[]::new));
    }

    public static Specification<Producto> soloDescuento(boolean activo) {
        if (!activo) {
            return (root, query, cb) -> cb.conjunction();
        }
        return (root, query, cb) -> cb.and(
                cb.isNotNull(root.get("descuentoPorcentaje")),
                cb.greaterThan(root.<BigDecimal>get("descuentoPorcentaje"), BigDecimal.ZERO));
    }

    public static Specification<Producto> soloDisponible(boolean activo) {
        if (!activo) {
            return (root, query, cb) -> cb.conjunction();
        }
        return (root, query, cb) -> cb.equal(root.get("disponibilidad"), EstadoDisponibilidad.DISPONIBLE);
    }

    public static Specification<Producto> soloDestacados(boolean activo) {
        if (!activo) {
            return (root, query, cb) -> cb.conjunction();
        }
        return (root, query, cb) -> cb.isTrue(root.get("destacado"));
    }

    public static Specification<Producto> modalidad(ModalidadVenta modalidad) {
        return (root, query, cb) -> modalidad == null ? cb.conjunction() : cb.equal(root.get("modalidad"), modalidad);
    }

    public static Specification<Producto> facilInstalacion(Boolean facil) {
        return (root, query, cb) -> facil == null ? cb.conjunction() : cb.equal(root.get("facilInstalacion"), facil);
    }

    public static Specification<Producto> rangoPrecio(BigDecimal minimo, BigDecimal maximo) {
        return (root, query, cb) -> cb.and(
                minimo == null ? cb.conjunction() : cb.ge(root.get("precioBase"), minimo),
                maximo == null ? cb.conjunction() : cb.le(root.get("precioBase"), maximo));
    }

    /** Productos con algún acabado cuyo nombre contenga cualquiera de los textos indicados (separados por coma). */
    public static Specification<Producto> acabado(String nombres) {
        List<String> values = splitValues(nombres);
        if (values.isEmpty()) {
            return (root, query, cb) -> cb.conjunction();
        }
        return (root, query, cb) -> {
            Subquery<UUID> subquery = query.subquery(UUID.class);
            Root<Producto> producto = subquery.correlate(root);
            subquery.select(producto.get("id"));
            var variante = producto.join("variantes");
            subquery.where(cb.or(values.stream().map(value -> cb.like(
                    sinTildes(cb, variante.<String>get(FIELD_NAME)),
                    "%" + normalizarTexto(value).replace("!", "!!").replace("%", "!%").replace("_", "!_") + "%",
                    '!')).toArray(jakarta.persistence.criteria.Predicate[]::new)));
            return cb.exists(subquery);
        };
    }

    /**
     * Fija el ORDER BY de la consulta principal según el orden solicitado. No debe tocar la
     * consulta de conteo (resultType == Long) para no romper el total de la paginación.
     */
    public static Specification<Producto> ordenadoPor(OrdenCatalogoProducto orden) {
        return (root, query, cb) -> {
            if (query.getResultType() != Long.class && query.getResultType() != long.class) {
                query.orderBy(buildOrders(orden, root, cb));
            }
            return cb.conjunction();
        };
    }

    private static List<Order> buildOrders(OrdenCatalogoProducto orden, Root<Producto> root, CriteriaBuilder cb) {
        Path<String> nombre = root.get(FIELD_NAME);
        Path<UUID> id = root.get("id");
        Path<BigDecimal> precioBase = root.get("precioBase");
        return switch (orden) {
            case PRECIO_ASC -> List.of(
                    cb.asc(nullsLast(cb, precioBase)), cb.asc(precioBase), cb.asc(nombre), cb.asc(id));
            case PRECIO_DESC -> List.of(
                    cb.asc(nullsLast(cb, precioBase)), cb.desc(precioBase), cb.asc(nombre), cb.asc(id));
            case NOMBRE_ASC, DESTACADOS -> List.of(cb.asc(nombre), cb.asc(id));
        };
    }

    private static Expression<Integer> nullsLast(CriteriaBuilder cb, Path<BigDecimal> campo) {
        return cb.<Integer>selectCase().when(cb.isNull(campo), 1).otherwise(0);
    }
}
