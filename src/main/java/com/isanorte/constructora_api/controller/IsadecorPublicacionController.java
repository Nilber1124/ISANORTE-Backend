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
import com.isanorte.constructora_api.dto.request.IsadecorPublicacionRequest;
import com.isanorte.constructora_api.dto.response.IsadecorPublicacionResponse;
import com.isanorte.constructora_api.service.IIsadecorPublicacionService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/** Administración de: publicaciones de redes sociales de la tienda ISADECOR (requiere rol administrativo). */
@RestController
@RequestMapping("/api/isadecor/publicaciones")
@RequiredArgsConstructor
public class IsadecorPublicacionController {

    private final IIsadecorPublicacionService service;

    @GetMapping
    public ResponseEntity<List<IsadecorPublicacionResponse>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<IsadecorPublicacionResponse> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PostMapping
    public ResponseEntity<IsadecorPublicacionResponse> create(@Valid @RequestBody IsadecorPublicacionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<IsadecorPublicacionResponse> update(
            @PathVariable UUID id, @Valid @RequestBody IsadecorPublicacionRequest request) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @PatchMapping("/{id}/activo")
    public ResponseEntity<IsadecorPublicacionResponse> updateActivo(
            @PathVariable UUID id, @Valid @RequestBody ActivoRequest request) {
        return ResponseEntity.ok(service.updateActivo(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
