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

import com.isanorte.constructora_api.dto.request.CotizacionRequest;
import com.isanorte.constructora_api.dto.response.CotizacionResponse;
import com.isanorte.constructora_api.enums.EstadoCotizacion;
import com.isanorte.constructora_api.mapper.CotizacionMapper;
import com.isanorte.constructora_api.service.ICotizacionService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/cotizaciones")
@RequiredArgsConstructor
public class CotizacionController {

    private final ICotizacionService cotizacionService;
    private final CotizacionMapper cotizacionMapper;

    @GetMapping
    public List<CotizacionResponse> findAll() {
        return cotizacionService.findAll().stream().map(cotizacionMapper::toResponse).toList();
    }

    @GetMapping("/{id}")
    public CotizacionResponse findById(@PathVariable UUID id) {
        return cotizacionMapper.toResponse(cotizacionService.findById(id));
    }

    @GetMapping("/codigo/{codigo}")
    public CotizacionResponse findByCodigo(@PathVariable String codigo) {
        return cotizacionMapper.toResponse(cotizacionService.findByCodigo(codigo));
    }

    @GetMapping("/estado/{estado}")
    public List<CotizacionResponse> findByEstado(@PathVariable EstadoCotizacion estado) {
        return cotizacionService.findByEstado(estado).stream().map(cotizacionMapper::toResponse).toList();
    }

    @PostMapping
    public ResponseEntity<CotizacionResponse> create(@Valid @RequestBody CotizacionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(cotizacionMapper.toResponse(cotizacionService.create(request)));
    }
}
