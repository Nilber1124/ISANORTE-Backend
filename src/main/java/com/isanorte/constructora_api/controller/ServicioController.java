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

import com.isanorte.constructora_api.dto.request.ServicioRequest;
import com.isanorte.constructora_api.dto.response.ServicioResponse;
import com.isanorte.constructora_api.mapper.ServicioMapper;
import com.isanorte.constructora_api.service.IServicioService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/servicios")
@RequiredArgsConstructor
public class ServicioController {

    private final IServicioService servicioService;
    private final ServicioMapper servicioMapper;

    @GetMapping
    public List<ServicioResponse> findAll() {
        return servicioService.findAll().stream().map(servicioMapper::toResponse).toList();
    }

    @GetMapping("/{id}")
    public ServicioResponse findById(@PathVariable UUID id) {
        return servicioMapper.toResponse(servicioService.findById(id));
    }

    @GetMapping("/slug/{slug}")
    public ServicioResponse findBySlug(@PathVariable String slug) {
        return servicioMapper.toResponse(servicioService.findBySlug(slug));
    }

    @PostMapping
    public ResponseEntity<ServicioResponse> create(@Valid @RequestBody ServicioRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(servicioMapper.toResponse(servicioService.create(request)));
    }
}
