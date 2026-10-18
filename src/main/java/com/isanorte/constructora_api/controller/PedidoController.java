package com.isanorte.constructora_api.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.isanorte.constructora_api.dto.request.EstadoPedidoRequest;
import com.isanorte.constructora_api.dto.response.PedidoResponse;
import com.isanorte.constructora_api.enums.EstadoPedido;
import com.isanorte.constructora_api.service.IPedidoService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/pedidos")
@RequiredArgsConstructor
public class PedidoController {

    private final IPedidoService pedidoService;

    @GetMapping
    public ResponseEntity<List<PedidoResponse>> findAll() {
        return ResponseEntity.ok(pedidoService.findAllResponse());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PedidoResponse> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(pedidoService.findByIdResponse(id));
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<PedidoResponse>> findByEstado(@PathVariable EstadoPedido estado) {
        return ResponseEntity.ok(pedidoService.findByEstadoResponse(estado));
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<PedidoResponse> updateEstado(
            @PathVariable UUID id, @Valid @RequestBody EstadoPedidoRequest request) {
        return ResponseEntity.ok(pedidoService.updateEstado(id, request));
    }
}
