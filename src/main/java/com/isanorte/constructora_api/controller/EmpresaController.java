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

import com.isanorte.constructora_api.dto.request.EmpresaRequest;
import com.isanorte.constructora_api.dto.response.EmpresaResponse;
import com.isanorte.constructora_api.mapper.EmpresaMapper;
import com.isanorte.constructora_api.service.IEmpresaService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/empresa")
@RequiredArgsConstructor
public class EmpresaController {

    private final IEmpresaService empresaService;
    private final EmpresaMapper empresaMapper;

    @GetMapping
    public List<EmpresaResponse> findAll() {
        return empresaService.findAll().stream().map(empresaMapper::toResponse).toList();
    }

    @GetMapping("/{id}")
    public EmpresaResponse findById(@PathVariable UUID id) {
        return empresaMapper.toResponse(empresaService.findById(id));
    }

    @PostMapping
    public ResponseEntity<EmpresaResponse> create(@Valid @RequestBody EmpresaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(empresaMapper.toResponse(empresaService.create(request)));
    }
}
