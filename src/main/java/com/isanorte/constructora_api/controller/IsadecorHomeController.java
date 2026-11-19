package com.isanorte.constructora_api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.isanorte.constructora_api.dto.response.IsadecorHomeResponse;
import com.isanorte.constructora_api.service.IIsadecorHomeService;

import lombok.RequiredArgsConstructor;

/** Lectura pública de la Home de una unidad de negocio (ISADECOR). */
@RestController
@RequestMapping("/api/publico/sitios")
@RequiredArgsConstructor
public class IsadecorHomeController {

    private final IIsadecorHomeService service;

    @GetMapping("/{clave}/unidades/{unidadSlug}/home")
    public ResponseEntity<IsadecorHomeResponse> findHome(@PathVariable String clave, @PathVariable String unidadSlug) {
        return ResponseEntity.ok(service.findHome(clave, unidadSlug));
    }
}
