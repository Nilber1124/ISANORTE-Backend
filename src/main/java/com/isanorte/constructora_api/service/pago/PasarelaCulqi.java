package com.isanorte.constructora_api.service.pago;

import java.math.BigDecimal;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * Esqueleto de la integración real con Culqi. Se activa con {@code PAGOS_PROVEEDOR=culqi}. Falta, cuando el
 * cliente entregue su cuenta de comercio: la llave secreta (variable de entorno) y la llamada a la API de
 * cargos con el token recibido. Ver docs/PAGOS_Y_AVISOS.md.
 */
@Component
@ConditionalOnProperty(name = "app.pagos.proveedor", havingValue = "culqi")
public class PasarelaCulqi implements PasarelaPago {

    @Override
    public String proveedor() {
        return "culqi";
    }

    @Override
    public ResultadoCobro cobrar(String codigoPedido, BigDecimal monto, String token) {
        throw new IllegalStateException("La pasarela Culqi aún no tiene credenciales configuradas");
    }
}
