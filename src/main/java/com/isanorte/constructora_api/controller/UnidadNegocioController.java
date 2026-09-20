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
import com.isanorte.constructora_api.dto.request.UnidadNegocioRequest;
import com.isanorte.constructora_api.dto.request.UnidadNegocioUpdateRequest;
import com.isanorte.constructora_api.dto.response.UnidadNegocioResponse;
import com.isanorte.constructora_api.service.IUnidadNegocioService;
import com.isanorte.constructora_api.dto.request.RecursoUnidadNegocioRequest;
import com.isanorte.constructora_api.dto.response.RecursoUnidadNegocioResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/unidades-negocio")
@RequiredArgsConstructor
public class UnidadNegocioController {

    private final IUnidadNegocioService unidadNegocioService;

    @GetMapping
    public ResponseEntity<List<UnidadNegocioResponse>> findAll() {
        return ResponseEntity.ok(unidadNegocioService.findAllResponse());
    }

    @GetMapping("/activas")
    public ResponseEntity<List<UnidadNegocioResponse>> findActive() {
        return ResponseEntity.ok(unidadNegocioService.findActiveResponses());
    }

    @GetMapping("/activas/slug/{slug}")
    public ResponseEntity<UnidadNegocioResponse> findActiveBySlug(@PathVariable String slug) {
        return ResponseEntity.ok(unidadNegocioService.findActiveBySlugResponse(slug));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UnidadNegocioResponse> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(unidadNegocioService.findByIdResponse(id));
    }

    @GetMapping("/slug/{slug}")
    public ResponseEntity<UnidadNegocioResponse> findBySlug(@PathVariable String slug) {
        return ResponseEntity.ok(unidadNegocioService.findBySlugResponse(slug));
    }

    @PostMapping
    public ResponseEntity<UnidadNegocioResponse> create(@Valid @RequestBody UnidadNegocioRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(unidadNegocioService.createResponse(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UnidadNegocioResponse> update(
            @PathVariable UUID id, @Valid @RequestBody UnidadNegocioUpdateRequest request) {
        return ResponseEntity.ok(unidadNegocioService.update(id, request));
    }

    @PatchMapping("/{id}/activo")
    public ResponseEntity<UnidadNegocioResponse> updateActivo(
            @PathVariable UUID id, @Valid @RequestBody ActivoRequest request) {
        return ResponseEntity.ok(unidadNegocioService.updateActivo(id, request));
    }

    @PostMapping("/{unidadId}/recursos")
    public ResponseEntity<RecursoUnidadNegocioResponse> createRecurso(@PathVariable UUID unidadId,
            @Valid @RequestBody RecursoUnidadNegocioRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(unidadNegocioService.createRecurso(unidadId, request));
    }

    @PutMapping("/{unidadId}/recursos/{recursoId}")
    public ResponseEntity<RecursoUnidadNegocioResponse> updateRecurso(@PathVariable UUID unidadId,
            @PathVariable UUID recursoId, @Valid @RequestBody RecursoUnidadNegocioRequest request) {
        return ResponseEntity.ok(unidadNegocioService.updateRecurso(unidadId, recursoId, request));
    }

    @DeleteMapping("/{unidadId}/recursos/{recursoId}")
    public ResponseEntity<Void> deleteRecurso(@PathVariable UUID unidadId, @PathVariable UUID recursoId) {
        unidadNegocioService.deleteRecurso(unidadId, recursoId);
        return ResponseEntity.noContent().build();
    }
}
