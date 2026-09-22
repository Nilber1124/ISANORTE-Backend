package com.isanorte.constructora_api.service.implementation;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.OffsetDateTime;
import org.springframework.stereotype.Service;
import com.isanorte.constructora_api.dto.request.ComparacionPrecioRequest;
import com.isanorte.constructora_api.dto.response.ComparacionPrecioResponse;
import com.isanorte.constructora_api.enums.EstadoComparacionPrecio;
import com.isanorte.constructora_api.exception.ScrapingPrecioException;
import com.isanorte.constructora_api.service.IComparacionPrecioService;
import com.isanorte.constructora_api.service.IPublicContentService;
import com.isanorte.constructora_api.service.IScrapingPrecioService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ComparacionPrecioService implements IComparacionPrecioService {
    /** V1: precioBase is denominated in soles. No currency conversion is performed. */
    public static final String MONEDA_INTERNA = "PEN";
    private final IPublicContentService publicContentService;
    private final IScrapingPrecioService scrapingService;

    @Override
    public ComparacionPrecioResponse comparar(String clave, String unidadSlug, String productoSlug,
            ComparacionPrecioRequest request) {
        // Reuse published-product/site/company/unit checks. Its read transaction ends before HTTP.
        var producto = publicContentService.findPublicProduct(clave, unidadSlug, productoSlug);
        var interno = producto.precioBase();
        try {
            var externo = scrapingService.extraer(request.urlExterna());
            EstadoComparacionPrecio estado;
            String mensaje;
            BigDecimal diferencia = null;
            BigDecimal porcentaje = null;
            if (interno == null || interno.signum() < 0 || externo.precio().signum() < 0) {
                estado = EstadoComparacionPrecio.NOT_COMPARABLE;
                mensaje = "El producto no tiene un precio interno válido para comparar.";
            } else if (externo.moneda() == null) {
                estado = EstadoComparacionPrecio.CURRENCY_UNKNOWN;
                mensaje = "No se pudo determinar la moneda externa; no es posible comparar los precios.";
            } else if (!MONEDA_INTERNA.equals(externo.moneda())) {
                estado = EstadoComparacionPrecio.CURRENCY_MISMATCH;
                mensaje = "Los precios pertenecen a monedas diferentes; no es posible compararlos.";
            } else {
                estado = EstadoComparacionPrecio.SUCCESS;
                diferencia = externo.precio().subtract(interno).setScale(2, RoundingMode.HALF_UP);
                // Signed percentage relative to ISADECOR: (external - internal) * 100 / internal.
                if (interno.signum() > 0) {
                    porcentaje = diferencia.multiply(BigDecimal.valueOf(100))
                            .divide(interno, 2, RoundingMode.HALF_UP);
                }
                String monto = "S/ " + diferencia.abs().toPlainString();
                mensaje = diferencia.signum() > 0 ? "ISADECOR tiene un precio " + monto + " menor."
                        : diferencia.signum() < 0 ? "El precio externo es " + monto + " menor."
                        : "Ambos precios son iguales.";
            }
            return new ComparacionPrecioResponse(producto.nombre(), externo.url(), externo.dominio(),
                    externo.nombreProducto(), interno, externo.precio(), MONEDA_INTERNA, externo.moneda(),
                    diferencia, porcentaje, estado == EstadoComparacionPrecio.SUCCESS, estado, mensaje,
                    OffsetDateTime.now());
        } catch (ScrapingPrecioException ex) {
            // Expected external failures are typed outcomes, not HTTP 500. Do not echo unsafe URLs.
            return new ComparacionPrecioResponse(producto.nombre(), null, null, null, interno, null,
                    MONEDA_INTERNA, null, null, null, false, ex.getEstado(), ex.getMessage(), OffsetDateTime.now());
        }
    }
}

