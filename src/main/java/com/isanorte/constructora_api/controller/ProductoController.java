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

import com.isanorte.constructora_api.dto.request.EstadoPublicacionRequest;
import com.isanorte.constructora_api.dto.request.ConfiguracionCalculoRequest;
import com.isanorte.constructora_api.dto.request.DocumentoProductoRequest;
import com.isanorte.constructora_api.dto.request.EspecificacionProductoRequest;
import com.isanorte.constructora_api.dto.request.ImagenProductoRequest;
import com.isanorte.constructora_api.dto.request.ProductoRequest;
import com.isanorte.constructora_api.dto.request.ProductoUpdateRequest;
import com.isanorte.constructora_api.dto.request.VarianteProductoRequest;
import com.isanorte.constructora_api.dto.response.ConfiguracionCalculoResponse;
import com.isanorte.constructora_api.dto.response.DocumentoProductoResponse;
import com.isanorte.constructora_api.dto.response.EspecificacionProductoResponse;
import com.isanorte.constructora_api.dto.response.ImagenProductoResponse;
import com.isanorte.constructora_api.dto.response.ProductoResponse;
import com.isanorte.constructora_api.dto.response.VarianteProductoResponse;
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

    @PostMapping("/{productoId}/variantes")
    public ResponseEntity<VarianteProductoResponse> createVariante(
            @PathVariable UUID productoId, @Valid @RequestBody VarianteProductoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productoService.createVariante(productoId, request));
    }

    @PutMapping("/{productoId}/variantes/{varianteId}")
    public ResponseEntity<VarianteProductoResponse> updateVariante(
            @PathVariable UUID productoId,
            @PathVariable UUID varianteId,
            @Valid @RequestBody VarianteProductoRequest request) {
        return ResponseEntity.ok(productoService.updateVariante(productoId, varianteId, request));
    }

    @DeleteMapping("/{productoId}/variantes/{varianteId}")
    public ResponseEntity<Void> deleteVariante(@PathVariable UUID productoId, @PathVariable UUID varianteId) {
        productoService.deleteVariante(productoId, varianteId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{productoId}/imagenes")
    public ResponseEntity<ImagenProductoResponse> createImagen(
            @PathVariable UUID productoId, @Valid @RequestBody ImagenProductoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productoService.createImagen(productoId, request));
    }

    @PutMapping("/{productoId}/imagenes/{imagenId}")
    public ResponseEntity<ImagenProductoResponse> updateImagen(
            @PathVariable UUID productoId,
            @PathVariable UUID imagenId,
            @Valid @RequestBody ImagenProductoRequest request) {
        return ResponseEntity.ok(productoService.updateImagen(productoId, imagenId, request));
    }

    @DeleteMapping("/{productoId}/imagenes/{imagenId}")
    public ResponseEntity<Void> deleteImagen(@PathVariable UUID productoId, @PathVariable UUID imagenId) {
        productoService.deleteImagen(productoId, imagenId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{productoId}/especificaciones")
    public ResponseEntity<EspecificacionProductoResponse> createEspecificacion(
            @PathVariable UUID productoId, @Valid @RequestBody EspecificacionProductoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(productoService.createEspecificacion(productoId, request));
    }

    @PutMapping("/{productoId}/especificaciones/{especificacionId}")
    public ResponseEntity<EspecificacionProductoResponse> updateEspecificacion(
            @PathVariable UUID productoId,
            @PathVariable UUID especificacionId,
            @Valid @RequestBody EspecificacionProductoRequest request) {
        return ResponseEntity.ok(productoService.updateEspecificacion(productoId, especificacionId, request));
    }

    @DeleteMapping("/{productoId}/especificaciones/{especificacionId}")
    public ResponseEntity<Void> deleteEspecificacion(
            @PathVariable UUID productoId, @PathVariable UUID especificacionId) {
        productoService.deleteEspecificacion(productoId, especificacionId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{productoId}/documentos")
    public ResponseEntity<DocumentoProductoResponse> createDocumento(
            @PathVariable UUID productoId, @Valid @RequestBody DocumentoProductoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productoService.createDocumento(productoId, request));
    }

    @PutMapping("/{productoId}/documentos/{documentoId}")
    public ResponseEntity<DocumentoProductoResponse> updateDocumento(
            @PathVariable UUID productoId,
            @PathVariable UUID documentoId,
            @Valid @RequestBody DocumentoProductoRequest request) {
        return ResponseEntity.ok(productoService.updateDocumento(productoId, documentoId, request));
    }

    @DeleteMapping("/{productoId}/documentos/{documentoId}")
    public ResponseEntity<Void> deleteDocumento(@PathVariable UUID productoId, @PathVariable UUID documentoId) {
        productoService.deleteDocumento(productoId, documentoId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{productoId}/configuracion-calculo")
    public ResponseEntity<ConfiguracionCalculoResponse> upsertConfiguracionCalculo(
            @PathVariable UUID productoId, @Valid @RequestBody ConfiguracionCalculoRequest request) {
        return ResponseEntity.ok(productoService.upsertConfiguracionCalculo(productoId, request));
    }
}
