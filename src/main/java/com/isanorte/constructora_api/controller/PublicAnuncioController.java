package com.isanorte.constructora_api.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.isanorte.constructora_api.dto.response.AnuncioResponse;
import com.isanorte.constructora_api.enums.DestinoAnuncio;
import com.isanorte.constructora_api.service.IAnuncioService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/publico/anuncios")
@RequiredArgsConstructor
public class PublicAnuncioController {

    private final IAnuncioService anuncioService;

    @GetMapping("/{destino}")
    public ResponseEntity<List<AnuncioResponse>> findPublicos(@PathVariable DestinoAnuncio destino) {
        return ResponseEntity.ok(anuncioService.findPublicos(destino));
    }
}
