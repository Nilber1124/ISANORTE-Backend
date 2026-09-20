package com.isanorte.constructora_api.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.isanorte.constructora_api.dto.request.EstadoSolicitudContactoRequest;
import com.isanorte.constructora_api.dto.response.SolicitudContactoResponse;
import com.isanorte.constructora_api.enums.EstadoSolicitudContacto;
import com.isanorte.constructora_api.service.ISolicitudContactoService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/solicitudes-contacto")
@RequiredArgsConstructor
public class SolicitudContactoController {
    private final ISolicitudContactoService service;

    @GetMapping public ResponseEntity<List<SolicitudContactoResponse>> findAll(
            @RequestParam(required = false) EstadoSolicitudContacto estado) {
        return ResponseEntity.ok(service.findAll(estado));
    }
    @GetMapping("/{id}") public ResponseEntity<SolicitudContactoResponse> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.findById(id));
    }
    @PatchMapping("/{id}/estado") public ResponseEntity<SolicitudContactoResponse> updateEstado(@PathVariable UUID id,
            @Valid @RequestBody EstadoSolicitudContactoRequest request) {
        return ResponseEntity.ok(service.updateEstado(id, request));
    }
}
