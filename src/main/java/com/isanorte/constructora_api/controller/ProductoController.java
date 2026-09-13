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

import com.isanorte.constructora_api.dto.request.ProductoRequest;
import com.isanorte.constructora_api.dto.response.ProductoResponse;
import com.isanorte.constructora_api.mapper.ProductoMapper;
import com.isanorte.constructora_api.service.IProductoService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/productos")
@RequiredArgsConstructor
public class ProductoController {

    private final IProductoService productoService;
    private final ProductoMapper productoMapper;

    @GetMapping
    public List<ProductoResponse> findAll() {
        return productoService.findAll().stream().map(productoMapper::toResponse).toList();
    }

    @GetMapping("/{id}")
    public ProductoResponse findById(@PathVariable UUID id) {
        return productoMapper.toResponse(productoService.findById(id));
    }

    @GetMapping("/slug/{slug}")
    public ProductoResponse findBySlug(@PathVariable String slug) {
        return productoMapper.toResponse(productoService.findBySlug(slug));
    }

    @GetMapping("/sku/{sku}")
    public ProductoResponse findBySku(@PathVariable String sku) {
        return productoMapper.toResponse(productoService.findBySku(sku));
    }

    @PostMapping
    public ResponseEntity<ProductoResponse> create(@Valid @RequestBody ProductoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productoMapper.toResponse(productoService.create(request)));
    }
}
