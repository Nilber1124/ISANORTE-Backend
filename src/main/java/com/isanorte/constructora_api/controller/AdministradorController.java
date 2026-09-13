package com.isanorte.constructora_api.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.isanorte.constructora_api.dto.response.AdministradorResponse;
import com.isanorte.constructora_api.mapper.AdministradorMapper;
import com.isanorte.constructora_api.service.IAdministradorService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/administradores")
@RequiredArgsConstructor
public class AdministradorController {

    private final IAdministradorService administradorService;
    private final AdministradorMapper administradorMapper;

    @GetMapping
    public List<AdministradorResponse> findAll() {
        return administradorService.findAll().stream().map(administradorMapper::toResponse).toList();
    }

    @GetMapping("/{id}")
    public AdministradorResponse findById(@PathVariable UUID id) {
        return administradorMapper.toResponse(administradorService.findById(id));
    }
}
