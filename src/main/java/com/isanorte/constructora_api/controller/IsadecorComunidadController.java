package com.isanorte.constructora_api.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.isanorte.constructora_api.dto.response.IsadecorHomeResponse.Publicacion;
import com.isanorte.constructora_api.service.IIsadecorComunidadService;

import lombok.RequiredArgsConstructor;

/** Lectura pública de los videos de redes sociales de una unidad de negocio (ISADECOR). */
@RestController
@RequestMapping("/api/publico/sitios")
@RequiredArgsConstructor
public class IsadecorComunidadController {

    private final IIsadecorComunidadService service;

    @GetMapping("/{clave}/unidades/{unidadSlug}/comunidad")
    public ResponseEntity<List<Publicacion>> findPublicaciones(@PathVariable String clave, @PathVariable String unidadSlug) {
        return ResponseEntity.ok(service.findPublicaciones(clave, unidadSlug));
    }
}
