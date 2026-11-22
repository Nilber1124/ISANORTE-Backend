package com.isanorte.constructora_api.service.pago;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/** Pasarela de demostración: el token "APROBADO" aprueba el cobro y cualquier otro lo rechaza. No cobra nada. */
@Component
@ConditionalOnProperty(name = "app.pagos.proveedor", havingValue = "simulado", matchIfMissing = true)
public class PasarelaSimulada implements PasarelaPago {

    public static final String TOKEN_APROBADO = "APROBADO";

    @Override
    public String proveedor() {
        return "simulado";
    }

    @Override
    public ResultadoCobro cobrar(String codigoPedido, BigDecimal monto, String token) {
        if (TOKEN_APROBADO.equals(token)) {
            return new ResultadoCobro(true, "SIM-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase(),
                    "Pago simulado aprobado");
        }
        return new ResultadoCobro(false, null, "Pago simulado rechazado");
    }
}
