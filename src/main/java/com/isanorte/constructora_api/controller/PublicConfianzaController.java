package com.isanorte.constructora_api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.isanorte.constructora_api.dto.response.PublicConfianzaResponse;
import com.isanorte.constructora_api.service.IPublicConfianzaService;

import lombok.RequiredArgsConstructor;

/** Lectura pública del contenido de confianza (FAQ, equipo, credenciales, testimonios, etc.). */
@RestController
@RequestMapping("/api/publico/sitios")
@RequiredArgsConstructor
public class PublicConfianzaController {

    private final IPublicConfianzaService service;

    @GetMapping("/{clave}/confianza")
    public ResponseEntity<PublicConfianzaResponse> findConfianza(@PathVariable String clave) {
        return ResponseEntity.ok(service.findConfianza(clave));
    }
}
