package com.isanorte.constructora_api.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.isanorte.constructora_api.dto.request.EmpresaRequest;
import com.isanorte.constructora_api.dto.request.EmpresaUpdateRequest;
import com.isanorte.constructora_api.dto.request.RedSocialRequest;
import com.isanorte.constructora_api.dto.response.EmpresaResponse;
import com.isanorte.constructora_api.dto.response.RedSocialResponse;
import com.isanorte.constructora_api.service.IEmpresaService;
import com.isanorte.constructora_api.dto.request.EstadisticaEmpresaRequest;
import com.isanorte.constructora_api.dto.response.EstadisticaEmpresaResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/empresa")
@RequiredArgsConstructor
public class EmpresaController {

    private final IEmpresaService empresaService;

    @GetMapping
    public ResponseEntity<List<EmpresaResponse>> findAll() {
        return ResponseEntity.ok(empresaService.findAllResponse());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmpresaResponse> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(empresaService.findByIdResponse(id));
    }

    @PostMapping
    public ResponseEntity<EmpresaResponse> create(@Valid @RequestBody EmpresaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(empresaService.createResponse(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EmpresaResponse> update(
            @PathVariable UUID id, @Valid @RequestBody EmpresaUpdateRequest request) {
        return ResponseEntity.ok(empresaService.update(id, request));
    }

    @PostMapping("/{empresaId}/redes-sociales")
    public ResponseEntity<RedSocialResponse> createRedSocial(
            @PathVariable UUID empresaId, @Valid @RequestBody RedSocialRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(empresaService.createRedSocial(empresaId, request));
    }

    @PutMapping("/{empresaId}/redes-sociales/{redSocialId}")
    public ResponseEntity<RedSocialResponse> updateRedSocial(
            @PathVariable UUID empresaId,
            @PathVariable UUID redSocialId,
            @Valid @RequestBody RedSocialRequest request) {
        return ResponseEntity.ok(empresaService.updateRedSocial(empresaId, redSocialId, request));
    }

    @DeleteMapping("/{empresaId}/redes-sociales/{redSocialId}")
    public ResponseEntity<Void> deleteRedSocial(
            @PathVariable UUID empresaId, @PathVariable UUID redSocialId) {
        empresaService.deleteRedSocial(empresaId, redSocialId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{empresaId}/estadisticas")
    public ResponseEntity<EstadisticaEmpresaResponse> createEstadistica(@PathVariable UUID empresaId,
            @Valid @RequestBody EstadisticaEmpresaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(empresaService.createEstadistica(empresaId, request));
    }

    @PutMapping("/{empresaId}/estadisticas/{estadisticaId}")
    public ResponseEntity<EstadisticaEmpresaResponse> updateEstadistica(@PathVariable UUID empresaId,
            @PathVariable UUID estadisticaId, @Valid @RequestBody EstadisticaEmpresaRequest request) {
        return ResponseEntity.ok(empresaService.updateEstadistica(empresaId, estadisticaId, request));
    }

    @DeleteMapping("/{empresaId}/estadisticas/{estadisticaId}")
    public ResponseEntity<Void> deleteEstadistica(@PathVariable UUID empresaId, @PathVariable UUID estadisticaId) {
        empresaService.deleteEstadistica(empresaId, estadisticaId);
        return ResponseEntity.noContent().build();
    }
}
