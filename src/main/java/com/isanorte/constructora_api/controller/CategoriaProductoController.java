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
import com.isanorte.constructora_api.dto.request.CategoriaProductoRequest;
import com.isanorte.constructora_api.dto.request.CategoriaProductoUpdateRequest;
import com.isanorte.constructora_api.dto.response.CategoriaProductoResponse;
import com.isanorte.constructora_api.service.ICategoriaProductoService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/categorias")
@RequiredArgsConstructor
public class CategoriaProductoController {

    private final ICategoriaProductoService categoriaProductoService;

    @GetMapping
    public ResponseEntity<List<CategoriaProductoResponse>> findAll() {
        return ResponseEntity.ok(categoriaProductoService.findAllResponse());
    }

    @GetMapping("/activas")
    public ResponseEntity<List<CategoriaProductoResponse>> findActive() {
        return ResponseEntity.ok(categoriaProductoService.findActiveResponses());
    }

    @GetMapping("/activas/slug/{slug}")
    public ResponseEntity<CategoriaProductoResponse> findActiveBySlug(@PathVariable String slug) {
        return ResponseEntity.ok(categoriaProductoService.findActiveBySlugResponse(slug));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoriaProductoResponse> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(categoriaProductoService.findByIdResponse(id));
    }

    @GetMapping("/slug/{slug}")
    public ResponseEntity<CategoriaProductoResponse> findBySlug(@PathVariable String slug) {
        return ResponseEntity.ok(categoriaProductoService.findBySlugResponse(slug));
    }

    @PostMapping
    public ResponseEntity<CategoriaProductoResponse> create(@Valid @RequestBody CategoriaProductoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(categoriaProductoService.createResponse(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoriaProductoResponse> update(
            @PathVariable UUID id, @Valid @RequestBody CategoriaProductoUpdateRequest request) {
        return ResponseEntity.ok(categoriaProductoService.update(id, request));
    }

    @PatchMapping("/{id}/activo")
    public ResponseEntity<CategoriaProductoResponse> updateActivo(
            @PathVariable UUID id, @Valid @RequestBody ActivoRequest request) {
        return ResponseEntity.ok(categoriaProductoService.updateActivo(id, request));
    }
}
