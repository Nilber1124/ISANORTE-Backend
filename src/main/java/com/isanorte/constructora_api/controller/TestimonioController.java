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
import com.isanorte.constructora_api.dto.request.TestimonioRequest;
import com.isanorte.constructora_api.dto.response.TestimonioResponse;
import com.isanorte.constructora_api.service.ITestimonioService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/** Administración de testimonio (requiere rol administrativo). */
@RestController
@RequestMapping("/api/testimonios")
@RequiredArgsConstructor
public class TestimonioController {

    private final ITestimonioService service;

    @GetMapping
    public ResponseEntity<List<TestimonioResponse>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TestimonioResponse> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PostMapping
    public ResponseEntity<TestimonioResponse> create(@Valid @RequestBody TestimonioRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TestimonioResponse> update(
            @PathVariable UUID id, @Valid @RequestBody TestimonioRequest request) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @PatchMapping("/{id}/activo")
    public ResponseEntity<TestimonioResponse> updateActivo(
            @PathVariable UUID id, @Valid @RequestBody ActivoRequest request) {
        return ResponseEntity.ok(service.updateActivo(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
