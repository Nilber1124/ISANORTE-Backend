package com.isanorte.constructora_api.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.isanorte.constructora_api.dto.request.ActivoRequest;
import com.isanorte.constructora_api.dto.request.GarantiaRequest;
import com.isanorte.constructora_api.dto.response.GarantiaResponse;
import com.isanorte.constructora_api.service.IGarantiaService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/** Administración de garantía (requiere rol administrativo). */
@RestController
@RequestMapping("/api/garantias")
@RequiredArgsConstructor
public class GarantiaController {

    private final IGarantiaService service;

    @GetMapping
    public ResponseEntity<List<GarantiaResponse>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<GarantiaResponse> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PostMapping
    public ResponseEntity<GarantiaResponse> create(@Valid @RequestBody GarantiaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<GarantiaResponse> update(
            @PathVariable UUID id, @Valid @RequestBody GarantiaRequest request) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @PatchMapping("/{id}/activo")
    public ResponseEntity<GarantiaResponse> updateActivo(
            @PathVariable UUID id, @Valid @RequestBody ActivoRequest request) {
        return ResponseEntity.ok(service.updateActivo(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
