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

import com.isanorte.constructora_api.dto.request.SeccionLandingRequest;
import com.isanorte.constructora_api.dto.response.SeccionLandingResponse;
import com.isanorte.constructora_api.mapper.SeccionLandingMapper;
import com.isanorte.constructora_api.service.ISeccionLandingService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/secciones-landing")
@RequiredArgsConstructor
public class SeccionLandingController {

    private final ISeccionLandingService seccionLandingService;
    private final SeccionLandingMapper seccionLandingMapper;

    @GetMapping
    public List<SeccionLandingResponse> findAll() {
        return seccionLandingService.findAll().stream().map(seccionLandingMapper::toResponse).toList();
    }

    @GetMapping("/visibles")
    public List<SeccionLandingResponse> findVisible() {
        return seccionLandingService.findByVisibleTrueOrderByOrdenAsc().stream()
                .map(seccionLandingMapper::toResponse).toList();
    }

    @GetMapping("/{id}")
    public SeccionLandingResponse findById(@PathVariable UUID id) {
        return seccionLandingMapper.toResponse(seccionLandingService.findById(id));
    }

    @PostMapping
    public ResponseEntity<SeccionLandingResponse> create(@Valid @RequestBody SeccionLandingRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(seccionLandingMapper.toResponse(seccionLandingService.create(request)));
    }
}
