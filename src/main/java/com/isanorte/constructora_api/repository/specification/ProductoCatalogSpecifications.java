package com.isanorte.constructora_api.repository.specification;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.domain.Specification;

import com.isanorte.constructora_api.enums.EstadoDisponibilidad;
import com.isanorte.constructora_api.enums.EstadoPublicacion;
import com.isanorte.constructora_api.enums.OrdenCatalogoProducto;
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

    private ProductoCatalogSpecifications() {
    }

    public static Specification<Producto> perteneceAUnidad(UUID unidadId) {
        return (root, query, cb) -> cb.equal(root.get("unidadNegocio").get("id"), unidadId);
    }

    public static Specification<Producto> estado(EstadoPublicacion estado) {
        return (root, query, cb) -> cb.equal(root.get("estado"), estado);
    }

    public static Specification<Producto> categoriaSlug(String slug, UUID unidadId) {
        if (slug == null || slug.isBlank()) {
            return (root, query, cb) -> cb.conjunction();
        }
        return (root, query, cb) -> {
            Subquery<UUID> subquery = query.subquery(UUID.class);
            Root<Producto> correlatedRoot = subquery.correlate(root);
            Join<Producto, CategoriaProducto> categoria = correlatedRoot.join("categorias");
            subquery.select(correlatedRoot.get("id"));
            subquery.where(cb.and(
                    cb.equal(categoria.get("slug"), slug),
                    cb.isTrue(categoria.get("activo")),
                    cb.or(cb.isNull(categoria.get("unidadNegocio")),
                            cb.equal(categoria.get("unidadNegocio").get("id"), unidadId))));
            return cb.exists(subquery);
        };
    }

    public static Specification<Producto> textoBusqueda(String termino) {
        if (termino == null || termino.isBlank()) {
            return (root, query, cb) -> cb.conjunction();
        }
        String patron = "%" + termino.trim().toLowerCase() + "%";
        return (root, query, cb) -> {
            Predicate coincideEnCampos = cb.or(
                    cb.like(cb.lower(root.get("nombre")), patron),
                    cb.like(cb.lower(root.get("sku")), patron),
                    cb.like(cb.lower(cb.coalesce(root.get("resumen"), "")), patron),
                    cb.like(cb.lower(root.get("descripcion")), patron));

            Subquery<UUID> subquery = query.subquery(UUID.class);
            Root<Producto> correlatedRoot = subquery.correlate(root);
            Join<Producto, CategoriaProducto> categoria = correlatedRoot.join("categorias");
            subquery.select(correlatedRoot.get("id"));
            subquery.where(cb.like(cb.lower(categoria.get("nombre")), patron));
            Predicate coincideEnCategoria = cb.exists(subquery);

            return cb.or(coincideEnCampos, coincideEnCategoria);
        };
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
        Path<String> nombre = root.get("nombre");
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
