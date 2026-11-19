package com.isanorte.constructora_api.util;

import java.util.Collection;

import com.isanorte.constructora_api.enums.EstadoDisponibilidad;
import com.isanorte.constructora_api.enums.EstadoStock;
import com.isanorte.constructora_api.model.Producto;
import com.isanorte.constructora_api.model.VarianteProducto;

/** Reglas puras de stock: estado visible y recálculo de la disponibilidad de un producto. */
public final class DisponibilidadStock {

    private DisponibilidadStock() {
    }

    /** Resumen del inventario de un producto: estado y unidades restantes (null si no se controla). */
    public record Resumen(EstadoStock estado, Integer quedan) {
    }

    /** Estado de un stock puntual frente a su umbral mínimo; un stock nulo significa sin control. */
    public static EstadoStock estado(Integer stock, Integer minimo) {
        if (stock == null) {
            return EstadoStock.SIN_CONTROL;
        }
        if (stock <= 0) {
            return EstadoStock.AGOTADO;
        }
        if (minimo != null && stock <= minimo) {
            return EstadoStock.QUEDAN;
        }
        return EstadoStock.DISPONIBLE;
    }

    /**
     * Inventario de un producto: si todas sus variantes controlan stock se suman; si no tiene variantes
     * se usa el stock del producto; en cualquier otro caso no hay control.
     */
    public static Resumen resumen(Producto producto) {
        Collection<VarianteProducto> variantes = producto.getVariantes();
        if (variantes != null && !variantes.isEmpty()) {
            boolean todasControladas = variantes.stream().allMatch(v -> v.getStockActual() != null);
            if (!todasControladas) {
                return new Resumen(EstadoStock.SIN_CONTROL, null);
            }
            int total = variantes.stream().mapToInt(VarianteProducto::getStockActual).sum();
            return new Resumen(estado(total, producto.getStockMinimo()), total);
        }
        return new Resumen(estado(producto.getStockActual(), producto.getStockMinimo()), producto.getStockActual());
    }

    /** Ajusta la disponibilidad del producto según el stock de sus variantes o el propio. */
    public static void recalcular(Producto producto) {
        Collection<VarianteProducto> variantes = producto.getVariantes();
        if (variantes != null && !variantes.isEmpty()) {
            boolean todasAgotadas = variantes.stream()
                    .allMatch(v -> v.getStockActual() != null && v.getStockActual() <= 0);
            if (todasAgotadas) {
                producto.setDisponibilidad(EstadoDisponibilidad.AGOTADO);
                return;
            }
        }
        Integer propio = producto.getStockActual();
        boolean hayStock = propio == null || propio > 0;
        if (producto.getDisponibilidad() == EstadoDisponibilidad.AGOTADO && hayStock) {
            producto.setDisponibilidad(EstadoDisponibilidad.DISPONIBLE);
        } else if (propio != null && propio <= 0) {
            producto.setDisponibilidad(EstadoDisponibilidad.AGOTADO);
        }
    }
}
