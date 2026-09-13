package com.isanorte.constructora_api.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.isanorte.constructora_api.dto.request.ActivoRequest;
import com.isanorte.constructora_api.dto.request.AdministradorUpdateRequest;
import com.isanorte.constructora_api.dto.response.AdministradorResponse;
import com.isanorte.constructora_api.service.IAdministradorService;

import lombok.RequiredArgsConstructor;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/administradores")
@RequiredArgsConstructor
public class AdministradorController {

    private final IAdministradorService administradorService;

    @GetMapping
    public ResponseEntity<List<AdministradorResponse>> findAll() {
        return ResponseEntity.ok(administradorService.findAllResponse());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AdministradorResponse> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(administradorService.findByIdResponse(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AdministradorResponse> update(
            @PathVariable UUID id, @Valid @RequestBody AdministradorUpdateRequest request) {
        return ResponseEntity.ok(administradorService.update(id, request));
    }

    @PatchMapping("/{id}/activo")
    public ResponseEntity<AdministradorResponse> updateActivo(
            @PathVariable UUID id, @Valid @RequestBody ActivoRequest request) {
        return ResponseEntity.ok(administradorService.updateActivo(id, request));
    }
}
