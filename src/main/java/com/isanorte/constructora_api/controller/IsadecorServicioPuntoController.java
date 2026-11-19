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
import com.isanorte.constructora_api.dto.request.IsadecorServicioPuntoRequest;
import com.isanorte.constructora_api.dto.response.IsadecorServicioPuntoResponse;
import com.isanorte.constructora_api.service.IIsadecorServicioPuntoService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/** Administración de: punto del bloque de servicios de la Home de ISADECOR (requiere rol administrativo). */
@RestController
@RequestMapping("/api/isadecor/servicio-puntos")
@RequiredArgsConstructor
public class IsadecorServicioPuntoController {

    private final IIsadecorServicioPuntoService service;

    @GetMapping
    public ResponseEntity<List<IsadecorServicioPuntoResponse>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<IsadecorServicioPuntoResponse> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PostMapping
    public ResponseEntity<IsadecorServicioPuntoResponse> create(@Valid @RequestBody IsadecorServicioPuntoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<IsadecorServicioPuntoResponse> update(
            @PathVariable UUID id, @Valid @RequestBody IsadecorServicioPuntoRequest request) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
