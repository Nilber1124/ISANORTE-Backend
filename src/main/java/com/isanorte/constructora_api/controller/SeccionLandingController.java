package com.isanorte.constructora_api.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.isanorte.constructora_api.dto.request.SeccionLandingRequest;
import com.isanorte.constructora_api.dto.request.SeccionLandingUpdateRequest;
import com.isanorte.constructora_api.dto.request.VisibleRequest;
import com.isanorte.constructora_api.dto.response.SeccionLandingResponse;
import com.isanorte.constructora_api.service.ISeccionLandingService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/secciones-landing")
@RequiredArgsConstructor
public class SeccionLandingController {

    private final ISeccionLandingService seccionLandingService;

    @GetMapping
    public ResponseEntity<List<SeccionLandingResponse>> findAll() {
        return ResponseEntity.ok(seccionLandingService.findAllResponse());
    }

    @GetMapping("/visibles")
    public ResponseEntity<List<SeccionLandingResponse>> findVisible() {
        return ResponseEntity.ok(seccionLandingService.findVisibleResponses());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SeccionLandingResponse> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(seccionLandingService.findByIdResponse(id));
    }

    @PostMapping
    public ResponseEntity<SeccionLandingResponse> create(@Valid @RequestBody SeccionLandingRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(seccionLandingService.createResponse(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SeccionLandingResponse> update(
            @PathVariable UUID id, @Valid @RequestBody SeccionLandingUpdateRequest request) {
        return ResponseEntity.ok(seccionLandingService.update(id, request));
    }

    @PatchMapping("/{id}/visible")
    public ResponseEntity<SeccionLandingResponse> updateVisible(
            @PathVariable UUID id, @Valid @RequestBody VisibleRequest request) {
        return ResponseEntity.ok(seccionLandingService.updateVisible(id, request));
    }
}
