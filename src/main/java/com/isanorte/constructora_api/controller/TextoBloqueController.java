package com.isanorte.constructora_api.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.isanorte.constructora_api.dto.request.TextoBloqueRequest;
import com.isanorte.constructora_api.dto.response.TextoBloqueResponse;
import com.isanorte.constructora_api.service.implementation.TextoBloqueService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/** Administración de los textos de cabecera de los bloques de la Home (requiere rol administrativo). */
@RestController
@RequestMapping("/api/textos-bloque")
@RequiredArgsConstructor
public class TextoBloqueController {

    private final TextoBloqueService service;

    @GetMapping
    public ResponseEntity<List<TextoBloqueResponse>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @PutMapping("/{clave}")
    public ResponseEntity<TextoBloqueResponse> upsert(
            @PathVariable String clave, @Valid @RequestBody TextoBloqueRequest request) {
        return ResponseEntity.ok(service.upsert(clave, request));
    }
}
