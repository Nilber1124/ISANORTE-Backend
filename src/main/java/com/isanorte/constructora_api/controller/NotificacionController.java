package com.isanorte.constructora_api.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.isanorte.constructora_api.dto.response.BandejaNotificacionesResponse;
import com.isanorte.constructora_api.dto.response.NotificacionResponse;
import com.isanorte.constructora_api.repository.NotificacionRepository;

import lombok.RequiredArgsConstructor;

/** Bandeja de avisos del administrador. Requiere rol administrativo. */
@RestController
@RequestMapping("/api/notificaciones")
@RequiredArgsConstructor
public class NotificacionController {

    private final NotificacionRepository repository;

    @GetMapping
    @Transactional(readOnly = true)
    public ResponseEntity<BandejaNotificacionesResponse> bandeja() {
        List<NotificacionResponse> items = repository.findTop50ByOrderByFechaCreacionDesc().stream()
                .map(n -> new NotificacionResponse(n.getId(), n.getTipo(), n.getTitulo(), n.getMensaje(),
                        n.getEnlace(), n.getLeida(), n.getFechaCreacion()))
                .toList();
        return ResponseEntity.ok(new BandejaNotificacionesResponse(repository.countByLeidaFalse(), items));
    }

    @PatchMapping("/leidas")
    @Transactional
    public ResponseEntity<Void> marcarLeidas() {
        repository.marcarTodasLeidas();
        return ResponseEntity.noContent().build();
    }
}
