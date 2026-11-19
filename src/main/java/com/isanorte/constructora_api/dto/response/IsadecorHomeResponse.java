package com.isanorte.constructora_api.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.isanorte.constructora_api.enums.EstadoStock;
import com.isanorte.constructora_api.enums.ModalidadVenta;
import com.isanorte.constructora_api.enums.RedPublicacion;
import com.isanorte.constructora_api.enums.TipoPromocion;
import com.isanorte.constructora_api.enums.UnidadVenta;

/** Contenido completo de la Home de una unidad de negocio, en una sola respuesta pública. */
public record IsadecorHomeResponse(
        Hero hero,
        List<Cinta> cinta,
        List<Promocion> promociones,
        Campana campana,
        SeccionProductos tienda,
        SeccionProductos facil,
        Servicios servicios,
        List<Resena> resenas,
        List<Publicacion> comunidad) {

    public record Hero(String titulo, String subtitulo) {
    }

    public record Cinta(String texto, String enlace) {
    }

    public record Promocion(TipoPromocion tipo, String titulo, String imagenUrl, LocalDate fechaFin, ProductoCard producto) {
    }

    public record Campana(String etiqueta, String titulo, String descripcion, String textoBoton, String enlace, String imagenUrl) {
    }

    public record SeccionProductos(String titulo, String textoEnlace, String categoriaEnlace, List<ProductoCard> productos) {
    }

    public record Servicios(String etiqueta, String titulo, String descripcion, List<String> puntos, String textoBoton, String enlace) {
    }

    public record Resena(String nombreCliente, Integer calificacion, String comentario, Boolean compraVerificada,
            String productoNombre, String productoSlug) {
    }

    /** Video de una red social; `red` es INSTAGRAM o TIKTOK. */
    public record Publicacion(RedPublicacion red, String url, String titulo, String miniaturaUrl) {
    }

    public record Categoria(String nombre, String slug) {
    }

    public record Imagen(String url, String alt) {
    }

    public record Acabado(String nombre, String colorHex, boolean agotado) {
    }

    public record ProductoCard(
            String slug, String nombre, List<Categoria> categorias, Imagen imagen,
            BigDecimal precioBase, BigDecimal precioAnterior, BigDecimal descuentoPorcentaje,
            UnidadVenta unidadVenta, ModalidadVenta modalidad, String plazoEntrega,
            Double valoracion, Long resenasCantidad, List<Acabado> acabados,
            EstadoStock estadoStock, Integer quedan) {
    }
}
