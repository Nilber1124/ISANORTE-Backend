package com.isanorte.constructora_api.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.isanorte.constructora_api.dto.request.CotizacionRequest;
import com.isanorte.constructora_api.dto.request.EstadoCotizacionRequest;
import com.isanorte.constructora_api.dto.response.CotizacionResponse;
import com.isanorte.constructora_api.enums.EstadoCotizacion;
import com.isanorte.constructora_api.service.ICotizacionService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/cotizaciones")
@RequiredArgsConstructor
public class CotizacionController {

    private final ICotizacionService cotizacionService;

    @GetMapping
    public ResponseEntity<List<CotizacionResponse>> findAll() {
        return ResponseEntity.ok(cotizacionService.findAllResponse());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CotizacionResponse> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(cotizacionService.findByIdResponse(id));
    }

    @GetMapping("/codigo/{codigo}")
    public ResponseEntity<CotizacionResponse> findByCodigo(@PathVariable String codigo) {
        return ResponseEntity.ok(cotizacionService.findByCodigoResponse(codigo));
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<CotizacionResponse>> findByEstado(@PathVariable EstadoCotizacion estado) {
        return ResponseEntity.ok(cotizacionService.findByEstadoResponse(estado));
    }

    @PostMapping
    public ResponseEntity<CotizacionResponse> create(@Valid @RequestBody CotizacionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(cotizacionService.createResponse(request));
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<CotizacionResponse> updateEstado(
            @PathVariable UUID id, @Valid @RequestBody EstadoCotizacionRequest request) {
        return ResponseEntity.ok(cotizacionService.updateEstado(id, request));
    }
}
