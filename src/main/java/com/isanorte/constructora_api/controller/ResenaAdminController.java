package com.isanorte.constructora_api.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.isanorte.constructora_api.dto.request.ResenaHomeRequest;
import com.isanorte.constructora_api.dto.response.ResenaAdminResponse;
import com.isanorte.constructora_api.service.IResenaAdminService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/** Reseñas desde administración: listado y selección de las que se muestran en la Home. */
@RestController
@RequestMapping("/api/resenas")
@RequiredArgsConstructor
public class ResenaAdminController {

    private final IResenaAdminService service;

    @GetMapping
    public ResponseEntity<List<ResenaAdminResponse>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @PatchMapping("/{id}/home")
    public ResponseEntity<ResenaAdminResponse> updateHome(
            @PathVariable UUID id, @Valid @RequestBody ResenaHomeRequest request) {
        return ResponseEntity.ok(service.updateHome(id, request));
    }
}
