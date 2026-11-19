package com.isanorte.constructora_api.mapper;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import com.isanorte.constructora_api.dto.response.IsadecorHomeResponse.Acabado;
import com.isanorte.constructora_api.dto.response.IsadecorHomeResponse.Categoria;
import com.isanorte.constructora_api.dto.response.IsadecorHomeResponse.Imagen;
import com.isanorte.constructora_api.dto.response.IsadecorHomeResponse.ProductoCard;
import com.isanorte.constructora_api.enums.EstadoDisponibilidad;
import com.isanorte.constructora_api.enums.EstadoStock;
import com.isanorte.constructora_api.model.ImagenProducto;
import com.isanorte.constructora_api.model.Producto;
import com.isanorte.constructora_api.model.VarianteProducto;
import com.isanorte.constructora_api.repository.projection.ResumenResena;
import com.isanorte.constructora_api.util.DisponibilidadStock;

/** Proyección compartida de las tarjetas públicas de producto. */
public final class ProductoCardAssembler {

    private ProductoCardAssembler() {
    }

    public static ProductoCard card(Producto p, Map<UUID, ResumenResena> resumenes) {
        ImagenProducto img = primaryImage(p);
        List<Categoria> categorias = p.getCategorias().stream()
                .sorted(Comparator.comparing(c -> c.getNombre()))
                .map(c -> new Categoria(c.getNombre(), c.getSlug())).toList();
        Collection<VarianteProducto> variantes = p.getVariantes();
        List<Acabado> acabados = variantes.stream()
                .sorted(Comparator.comparing(v -> v.getOrden() == null ? 0 : v.getOrden()))
                .map(v -> new Acabado(v.getNombre(), v.getColorHex(),
                        agotado(v)))
                .toList();
        ResumenResena r = resumenes.get(p.getId());
        var stock = stock(p);
        Integer quedan = stock.estado() == EstadoStock.QUEDAN ? stock.quedan() : null;
        return new ProductoCard(p.getSlug(), p.getNombre(), categorias,
                img == null ? null : new Imagen(img.getUrl(), img.getAltText()),
                p.getPrecioBase(), p.getPrecioAnterior(), p.getDescuentoPorcentaje(),
                p.getUnidadVenta(), p.getModalidad(), p.getPlazoEntrega(),
                valoracion(r),
                cantidad(r),
                acabados, stock.estado(), quedan);
    }

    public static ImagenProducto primaryImage(Producto p) {
        return p.getImagenes().stream()
                .sorted(Comparator.comparing((ImagenProducto i) -> !Boolean.TRUE.equals(i.getEsPrincipal()))
                        .thenComparing(i -> i.getOrden() == null ? 0 : i.getOrden()))
                .findFirst().orElse(null);
    }

    public static boolean agotado(VarianteProducto variante) {
        return Boolean.FALSE.equals(variante.getDisponible())
                || (variante.getStockActual() != null && variante.getStockActual() <= 0);
    }

    public static DisponibilidadStock.Resumen stock(Producto producto) {
        if (producto.getDisponibilidad() == EstadoDisponibilidad.AGOTADO
                || (!producto.getVariantes().isEmpty() && producto.getVariantes().stream().allMatch(ProductoCardAssembler::agotado))) {
            return new DisponibilidadStock.Resumen(EstadoStock.AGOTADO, null);
        }
        return DisponibilidadStock.resumen(producto);
    }

    public static Double valoracion(ResumenResena resumen) {
        return resumen == null || resumen.getPromedio() == null ? null : Math.round(resumen.getPromedio() * 10.0) / 10.0;
    }

    public static long cantidad(ResumenResena resumen) {
        return resumen == null ? 0L : resumen.getCantidad();
    }
}
