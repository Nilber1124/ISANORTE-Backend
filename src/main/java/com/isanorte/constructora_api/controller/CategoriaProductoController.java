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

import com.isanorte.constructora_api.dto.request.CategoriaProductoRequest;
import com.isanorte.constructora_api.dto.response.CategoriaProductoResponse;
import com.isanorte.constructora_api.mapper.CategoriaProductoMapper;
import com.isanorte.constructora_api.service.ICategoriaProductoService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/categorias")
@RequiredArgsConstructor
public class CategoriaProductoController {

    private final ICategoriaProductoService categoriaProductoService;
    private final CategoriaProductoMapper categoriaProductoMapper;

    @GetMapping
    public List<CategoriaProductoResponse> findAll() {
        return categoriaProductoService.findAll().stream().map(categoriaProductoMapper::toResponse).toList();
    }

    @GetMapping("/activas")
    public List<CategoriaProductoResponse> findActive() {
        return categoriaProductoService.findByActivoTrueOrderByOrdenAsc().stream()
                .map(categoriaProductoMapper::toResponse).toList();
    }

    @GetMapping("/{id}")
    public CategoriaProductoResponse findById(@PathVariable UUID id) {
        return categoriaProductoMapper.toResponse(categoriaProductoService.findById(id));
    }

    @GetMapping("/slug/{slug}")
    public CategoriaProductoResponse findBySlug(@PathVariable String slug) {
        return categoriaProductoMapper.toResponse(categoriaProductoService.findBySlug(slug));
    }

    @PostMapping
    public ResponseEntity<CategoriaProductoResponse> create(@Valid @RequestBody CategoriaProductoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(categoriaProductoMapper.toResponse(categoriaProductoService.create(request)));
    }
}
