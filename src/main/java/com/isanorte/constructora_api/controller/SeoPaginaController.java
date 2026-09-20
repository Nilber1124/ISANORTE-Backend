package com.isanorte.constructora_api.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.isanorte.constructora_api.dto.request.SeoPaginaRequest;
import com.isanorte.constructora_api.dto.request.SeoPaginaUpdateRequest;
import com.isanorte.constructora_api.dto.response.SeoPaginaResponse;
import com.isanorte.constructora_api.service.ISeoPaginaService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/seo-paginas")
@RequiredArgsConstructor
public class SeoPaginaController {
    private final ISeoPaginaService service;

    @GetMapping public ResponseEntity<List<SeoPaginaResponse>> findAll() { return ResponseEntity.ok(service.findAll()); }
    @GetMapping("/{id}") public ResponseEntity<SeoPaginaResponse> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.findById(id));
    }
    @PostMapping public ResponseEntity<SeoPaginaResponse> create(@Valid @RequestBody SeoPaginaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }
    @PutMapping("/{id}") public ResponseEntity<SeoPaginaResponse> update(@PathVariable UUID id,
            @Valid @RequestBody SeoPaginaUpdateRequest request) {
        return ResponseEntity.ok(service.update(id, request));
    }
}
