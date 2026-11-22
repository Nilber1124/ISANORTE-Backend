package com.isanorte.constructora_api.controller;

import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.isanorte.constructora_api.dto.response.TiendaConfigResponse;
import com.isanorte.constructora_api.service.TiendaConfig;
import com.isanorte.constructora_api.service.pago.PasarelaPago;

import lombok.RequiredArgsConstructor;

/** Datos públicos del checkout: costo de envío y si el pago es de demostración. */
@RestController
@RequestMapping("/api/publico/tienda")
@RequiredArgsConstructor
public class TiendaConfigController {

    private final TiendaConfig tiendaConfig;
    private final PasarelaPago pasarela;

    @GetMapping("/config")
    public ResponseEntity<TiendaConfigResponse> config() {
        return ResponseEntity.ok().cacheControl(CacheControl.noStore()).body(new TiendaConfigResponse(
                tiendaConfig.costoDelivery(), tiendaConfig.recojoHabilitado(), pasarela.proveedor(),
                "simulado".equals(pasarela.proveedor())));
    }
}
