package com.isanorte.constructora_api.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.isanorte.constructora_api.dto.request.ActivoRequest;
import com.isanorte.constructora_api.dto.request.ProyectoRequest;
import com.isanorte.constructora_api.dto.request.ProyectoUpdateRequest;
import com.isanorte.constructora_api.dto.response.ProyectoResponse;
import com.isanorte.constructora_api.service.IProyectoService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/proyectos")
@RequiredArgsConstructor
public class ProyectoController {

    private final IProyectoService proyectoService;

    @GetMapping
    public ResponseEntity<List<ProyectoResponse>> findAll() {
        return ResponseEntity.ok(proyectoService.findAllResponse());
    }

    @GetMapping("/activos")
    public ResponseEntity<List<ProyectoResponse>> findActive() {
        return ResponseEntity.ok(proyectoService.findActiveResponses());
    }

    @GetMapping("/activos/slug/{slug}")
    public ResponseEntity<ProyectoResponse> findActiveBySlug(@PathVariable String slug) {
        return ResponseEntity.ok(proyectoService.findActiveBySlugResponse(slug));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProyectoResponse> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(proyectoService.findByIdResponse(id));
    }

    @GetMapping("/slug/{slug}")
    public ResponseEntity<ProyectoResponse> findBySlug(@PathVariable String slug) {
        return ResponseEntity.ok(proyectoService.findBySlugResponse(slug));
    }

    @PostMapping
    public ResponseEntity<ProyectoResponse> create(@Valid @RequestBody ProyectoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(proyectoService.createResponse(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProyectoResponse> update(
            @PathVariable UUID id, @Valid @RequestBody ProyectoUpdateRequest request) {
        return ResponseEntity.ok(proyectoService.update(id, request));
    }

    @PatchMapping("/{id}/activo")
    public ResponseEntity<ProyectoResponse> updateActivo(
            @PathVariable UUID id, @Valid @RequestBody ActivoRequest request) {
        return ResponseEntity.ok(proyectoService.updateActivo(id, request));
    }
}
