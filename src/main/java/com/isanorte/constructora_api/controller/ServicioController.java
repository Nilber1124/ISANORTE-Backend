package com.isanorte.constructora_api.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.isanorte.constructora_api.dto.request.ActivoRequest;
import com.isanorte.constructora_api.dto.request.ServicioRequest;
import com.isanorte.constructora_api.dto.request.ServicioUpdateRequest;
import com.isanorte.constructora_api.dto.response.ServicioResponse;
import com.isanorte.constructora_api.service.IServicioService;
import com.isanorte.constructora_api.dto.request.BeneficioServicioRequest;
import com.isanorte.constructora_api.dto.response.BeneficioServicioResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/servicios")
@RequiredArgsConstructor
public class ServicioController {

    private final IServicioService servicioService;

    @GetMapping
    public ResponseEntity<List<ServicioResponse>> findAll() {
        return ResponseEntity.ok(servicioService.findAllResponse());
    }

    @GetMapping("/activos")
    public ResponseEntity<List<ServicioResponse>> findActive() {
        return ResponseEntity.ok(servicioService.findActiveResponses());
    }

    @GetMapping("/activos/slug/{slug}")
    public ResponseEntity<ServicioResponse> findActiveBySlug(@PathVariable String slug) {
        return ResponseEntity.ok(servicioService.findActiveBySlugResponse(slug));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ServicioResponse> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(servicioService.findByIdResponse(id));
    }

    @GetMapping("/slug/{slug}")
    public ResponseEntity<ServicioResponse> findBySlug(@PathVariable String slug) {
        return ResponseEntity.ok(servicioService.findBySlugResponse(slug));
    }

    @PostMapping
    public ResponseEntity<ServicioResponse> create(@Valid @RequestBody ServicioRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(servicioService.createResponse(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ServicioResponse> update(
            @PathVariable UUID id, @Valid @RequestBody ServicioUpdateRequest request) {
        return ResponseEntity.ok(servicioService.update(id, request));
    }

    @PatchMapping("/{id}/activo")
    public ResponseEntity<ServicioResponse> updateActivo(
            @PathVariable UUID id, @Valid @RequestBody ActivoRequest request) {
        return ResponseEntity.ok(servicioService.updateActivo(id, request));
    }

    @PostMapping("/{servicioId}/beneficios")
    public ResponseEntity<BeneficioServicioResponse> createBeneficio(@PathVariable UUID servicioId,
            @Valid @RequestBody BeneficioServicioRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(servicioService.createBeneficio(servicioId, request));
    }

    @PutMapping("/{servicioId}/beneficios/{beneficioId}")
    public ResponseEntity<BeneficioServicioResponse> updateBeneficio(@PathVariable UUID servicioId,
            @PathVariable UUID beneficioId, @Valid @RequestBody BeneficioServicioRequest request) {
        return ResponseEntity.ok(servicioService.updateBeneficio(servicioId, beneficioId, request));
    }

    @DeleteMapping("/{servicioId}/beneficios/{beneficioId}")
    public ResponseEntity<Void> deleteBeneficio(@PathVariable UUID servicioId, @PathVariable UUID beneficioId) {
        servicioService.deleteBeneficio(servicioId, beneficioId);
        return ResponseEntity.noContent().build();
    }
}
