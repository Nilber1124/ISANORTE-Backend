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

import com.isanorte.constructora_api.dto.request.ConfiguracionSitioRequest;
import com.isanorte.constructora_api.dto.request.ConfiguracionSitioUpdateRequest;
import com.isanorte.constructora_api.dto.response.ConfiguracionSitioResponse;
import com.isanorte.constructora_api.service.IConfiguracionSitioService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/configuracion-sitio")
@RequiredArgsConstructor
public class ConfiguracionSitioController {

    private final IConfiguracionSitioService configuracionSitioService;

    @GetMapping
    public ResponseEntity<List<ConfiguracionSitioResponse>> findAll() {
        return ResponseEntity.ok(configuracionSitioService.findAllResponse());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ConfiguracionSitioResponse> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(configuracionSitioService.findByIdResponse(id));
    }

    @PostMapping
    public ResponseEntity<ConfiguracionSitioResponse> create(
            @Valid @RequestBody ConfiguracionSitioRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(configuracionSitioService.createResponse(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ConfiguracionSitioResponse> update(
            @PathVariable UUID id, @Valid @RequestBody ConfiguracionSitioUpdateRequest request) {
        return ResponseEntity.ok(configuracionSitioService.update(id, request));
    }
}
