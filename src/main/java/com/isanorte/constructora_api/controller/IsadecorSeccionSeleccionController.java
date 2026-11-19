package com.isanorte.constructora_api.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.isanorte.constructora_api.dto.request.SeccionProductosIdsRequest;
import com.isanorte.constructora_api.dto.response.SeccionProductoItemResponse;
import com.isanorte.constructora_api.service.IIsadecorSeccionSeleccionService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/** Productos fijados y excluidos de una sección de la Home de ISADECOR (requiere rol administrativo). */
@RestController
@RequestMapping("/api/isadecor/secciones/{seccionId}")
@RequiredArgsConstructor
public class IsadecorSeccionSeleccionController {

    private final IIsadecorSeccionSeleccionService service;

    @GetMapping("/fijados")
    public ResponseEntity<List<SeccionProductoItemResponse>> fijados(@PathVariable UUID seccionId) {
        return ResponseEntity.ok(service.fijados(seccionId));
    }

    @PutMapping("/fijados")
    public ResponseEntity<List<SeccionProductoItemResponse>> reemplazarFijados(
            @PathVariable UUID seccionId, @Valid @RequestBody SeccionProductosIdsRequest request) {
        return ResponseEntity.ok(service.reemplazarFijados(seccionId, request));
    }

    @GetMapping("/excluidos")
    public ResponseEntity<List<SeccionProductoItemResponse>> excluidos(@PathVariable UUID seccionId) {
        return ResponseEntity.ok(service.excluidos(seccionId));
    }

    @PutMapping("/excluidos")
    public ResponseEntity<List<SeccionProductoItemResponse>> reemplazarExcluidos(
            @PathVariable UUID seccionId, @Valid @RequestBody SeccionProductosIdsRequest request) {
        return ResponseEntity.ok(service.reemplazarExcluidos(seccionId, request));
    }
}
