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
import com.isanorte.constructora_api.dto.request.ClienteMarcaRequest;
import com.isanorte.constructora_api.dto.response.ClienteMarcaResponse;
import com.isanorte.constructora_api.service.IClienteMarcaService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/** Administración de cliente o marca (requiere rol administrativo). */
@RestController
@RequestMapping("/api/clientes-marca")
@RequiredArgsConstructor
public class ClienteMarcaController {

    private final IClienteMarcaService service;

    @GetMapping
    public ResponseEntity<List<ClienteMarcaResponse>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClienteMarcaResponse> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PostMapping
    public ResponseEntity<ClienteMarcaResponse> create(@Valid @RequestBody ClienteMarcaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClienteMarcaResponse> update(
            @PathVariable UUID id, @Valid @RequestBody ClienteMarcaRequest request) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @PatchMapping("/{id}/activo")
    public ResponseEntity<ClienteMarcaResponse> updateActivo(
            @PathVariable UUID id, @Valid @RequestBody ActivoRequest request) {
        return ResponseEntity.ok(service.updateActivo(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
