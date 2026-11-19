package com.isanorte.constructora_api.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.isanorte.constructora_api.dto.request.AjusteStockRequest;
import com.isanorte.constructora_api.dto.response.MovimientoStockResponse;
import com.isanorte.constructora_api.dto.response.StockResponse;
import com.isanorte.constructora_api.service.IStockService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/** Ajuste de stock e historial de movimientos (requiere rol administrativo). */
@RestController
@RequestMapping("/api/productos/{productoId}")
@RequiredArgsConstructor
public class StockController {

    private final IStockService service;

    @PatchMapping("/stock")
    public ResponseEntity<StockResponse> ajustarProducto(
            @PathVariable UUID productoId, @Valid @RequestBody AjusteStockRequest request, Authentication authentication) {
        return ResponseEntity.ok(service.ajustarProducto(productoId, request, nombre(authentication)));
    }

    @PatchMapping("/variantes/{varianteId}/stock")
    public ResponseEntity<StockResponse> ajustarVariante(
            @PathVariable UUID productoId, @PathVariable UUID varianteId,
            @Valid @RequestBody AjusteStockRequest request, Authentication authentication) {
        return ResponseEntity.ok(service.ajustarVariante(productoId, varianteId, request, nombre(authentication)));
    }

    @GetMapping("/movimientos-stock")
    public ResponseEntity<List<MovimientoStockResponse>> historial(@PathVariable UUID productoId) {
        return ResponseEntity.ok(service.historial(productoId));
    }

    private String nombre(Authentication authentication) {
        return authentication == null ? null : authentication.getName();
    }
}
