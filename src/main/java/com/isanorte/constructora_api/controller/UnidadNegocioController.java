package com.isanorte.constructora_api.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.isanorte.constructora_api.dto.request.UnidadNegocioRequest;
import com.isanorte.constructora_api.dto.response.UnidadNegocioResponse;
import com.isanorte.constructora_api.mapper.UnidadNegocioMapper;
import com.isanorte.constructora_api.service.IUnidadNegocioService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/unidades-negocio")
@RequiredArgsConstructor
public class UnidadNegocioController {

    private final IUnidadNegocioService unidadNegocioService;
    private final UnidadNegocioMapper unidadNegocioMapper;

    @GetMapping
    public List<UnidadNegocioResponse> findAll() {
        return unidadNegocioService.findAll().stream().map(unidadNegocioMapper::toResponse).toList();
    }

    @GetMapping("/activas")
    public List<UnidadNegocioResponse> findActive() {
        return unidadNegocioService.findByActivoTrueOrderByOrdenAsc().stream()
                .map(unidadNegocioMapper::toResponse).toList();
    }

    @GetMapping("/{id}")
    public UnidadNegocioResponse findById(@PathVariable UUID id) {
        return unidadNegocioMapper.toResponse(unidadNegocioService.findById(id));
    }

    @GetMapping("/slug/{slug}")
    public UnidadNegocioResponse findBySlug(@PathVariable String slug) {
        return unidadNegocioMapper.toResponse(unidadNegocioService.findBySlug(slug));
    }

    @PostMapping
    public ResponseEntity<UnidadNegocioResponse> create(@Valid @RequestBody UnidadNegocioRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(unidadNegocioMapper.toResponse(unidadNegocioService.create(request)));
    }
}
