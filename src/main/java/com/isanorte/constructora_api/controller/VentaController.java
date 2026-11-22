package com.isanorte.constructora_api.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.isanorte.constructora_api.dto.request.AnulacionVentaRequest;
import com.isanorte.constructora_api.dto.request.VentaRequest;
import com.isanorte.constructora_api.dto.response.VentaResponse;
import com.isanorte.constructora_api.service.implementation.VentaService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/** Ventas registradas a mano (WhatsApp, tienda u otro canal). Requiere rol administrativo. */
@RestController
@RequestMapping("/api/ventas")
@RequiredArgsConstructor
public class VentaController {

    private final VentaService service;

    @GetMapping
    public ResponseEntity<List<VentaResponse>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<VentaResponse> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PostMapping
    public ResponseEntity<VentaResponse> create(@Valid @RequestBody VentaRequest request, Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request, nombre(authentication)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<VentaResponse> update(
            @PathVariable UUID id, @Valid @RequestBody VentaRequest request, Authentication authentication) {
        return ResponseEntity.ok(service.update(id, request, nombre(authentication)));
    }

    @PatchMapping("/{id}/anulada")
    public ResponseEntity<VentaResponse> setAnulada(
            @PathVariable UUID id, @Valid @RequestBody AnulacionVentaRequest request, Authentication authentication) {
        return ResponseEntity.ok(service.setAnulada(id, request.anulada(), nombre(authentication)));
    }

    private String nombre(Authentication authentication) {
        return authentication == null ? null : authentication.getName();
    }
}
