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

import com.isanorte.constructora_api.dto.request.EstadoPublicacionRequest;
import com.isanorte.constructora_api.dto.request.ProductoRequest;
import com.isanorte.constructora_api.dto.request.ProductoUpdateRequest;
import com.isanorte.constructora_api.dto.response.ProductoResponse;
import com.isanorte.constructora_api.service.IProductoService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/productos")
@RequiredArgsConstructor
public class ProductoController {

    private final IProductoService productoService;

    @GetMapping
    public ResponseEntity<List<ProductoResponse>> findAll() {
        return ResponseEntity.ok(productoService.findAllResponse());
    }

    @GetMapping("/publicados")
    public ResponseEntity<List<ProductoResponse>> findPublished() {
        return ResponseEntity.ok(productoService.findPublishedResponses());
    }

    @GetMapping("/publicados/slug/{slug}")
    public ResponseEntity<ProductoResponse> findPublishedBySlug(@PathVariable String slug) {
        return ResponseEntity.ok(productoService.findPublishedBySlugResponse(slug));
    }

    @GetMapping("/publicados/sku/{sku}")
    public ResponseEntity<ProductoResponse> findPublishedBySku(@PathVariable String sku) {
        return ResponseEntity.ok(productoService.findPublishedBySkuResponse(sku));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductoResponse> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(productoService.findByIdResponse(id));
    }

    @GetMapping("/slug/{slug}")
    public ResponseEntity<ProductoResponse> findBySlug(@PathVariable String slug) {
        return ResponseEntity.ok(productoService.findBySlugResponse(slug));
    }

    @GetMapping("/sku/{sku}")
    public ResponseEntity<ProductoResponse> findBySku(@PathVariable String sku) {
        return ResponseEntity.ok(productoService.findBySkuResponse(sku));
    }

    @PostMapping
    public ResponseEntity<ProductoResponse> create(@Valid @RequestBody ProductoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productoService.createResponse(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductoResponse> update(
            @PathVariable UUID id, @Valid @RequestBody ProductoUpdateRequest request) {
        return ResponseEntity.ok(productoService.update(id, request));
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<ProductoResponse> updateEstado(
            @PathVariable UUID id, @Valid @RequestBody EstadoPublicacionRequest request) {
        return ResponseEntity.ok(productoService.updateEstado(id, request));
    }
}
