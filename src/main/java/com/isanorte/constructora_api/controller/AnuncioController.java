package com.isanorte.constructora_api.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.isanorte.constructora_api.dto.request.ActivoRequest;
import com.isanorte.constructora_api.dto.request.AnuncioRequest;
import com.isanorte.constructora_api.dto.response.AnuncioResponse;
import com.isanorte.constructora_api.service.IAnuncioService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/anuncios")
@RequiredArgsConstructor
public class AnuncioController {

    private final IAnuncioService anuncioService;

    @GetMapping
    public ResponseEntity<List<AnuncioResponse>> findAll() {
        return ResponseEntity.ok(anuncioService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AnuncioResponse> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(anuncioService.findById(id));
    }

    @PostMapping
    public ResponseEntity<AnuncioResponse> create(@Valid @RequestBody AnuncioRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(anuncioService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AnuncioResponse> update(
            @PathVariable UUID id, @Valid @RequestBody AnuncioRequest request) {
        return ResponseEntity.ok(anuncioService.update(id, request));
    }

    @PatchMapping("/{id}/activo")
    public ResponseEntity<AnuncioResponse> updateActivo(
            @PathVariable UUID id, @Valid @RequestBody ActivoRequest request) {
        return ResponseEntity.ok(anuncioService.updateActivo(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        anuncioService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

