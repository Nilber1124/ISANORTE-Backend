package com.isanorte.constructora_api.service;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/** Reglas de envío de la tienda online, configurables por variables de entorno. */
@Component
public class TiendaConfig {

    private final BigDecimal costoDelivery;
    private final boolean recojoHabilitado;

    public TiendaConfig(
            @Value("${app.tienda.costo-delivery:10.00}") BigDecimal costoDelivery,
            @Value("${app.tienda.recojo-habilitado:true}") boolean recojoHabilitado) {
        this.costoDelivery = costoDelivery;
        this.recojoHabilitado = recojoHabilitado;
    }

    public BigDecimal costoDelivery() {
        return costoDelivery;
    }

    public boolean recojoHabilitado() {
        return recojoHabilitado;
    }
}
