package com.isanorte.constructora_api.service.pago;

import java.math.BigDecimal;

/**
 * Puerto de cobro online. {@code PasarelaSimulada} no mueve dinero (demostración); para cobrar de verdad se
 * implementa esta interfaz con el SDK de la pasarela elegida (Culqi, Izipay...) y se activa con
 * {@code PAGOS_PROVEEDOR}. El token lo genera el formulario de la pasarela en el navegador: los datos de la
 * tarjeta nunca pasan por nuestro servidor.
 */
public interface PasarelaPago {

    String proveedor();

    ResultadoCobro cobrar(String codigoPedido, BigDecimal monto, String token);

    record ResultadoCobro(boolean aprobado, String referencia, String mensaje) {
    }
}
