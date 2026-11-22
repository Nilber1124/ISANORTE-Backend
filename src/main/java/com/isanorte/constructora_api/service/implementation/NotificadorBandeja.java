package com.isanorte.constructora_api.service.implementation;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.isanorte.constructora_api.enums.TipoNotificacion;
import com.isanorte.constructora_api.model.Notificacion;
import com.isanorte.constructora_api.repository.NotificacionRepository;
import com.isanorte.constructora_api.service.Notificador;

import lombok.RequiredArgsConstructor;

/** Guarda el aviso en la bandeja de notificaciones del panel de administración. */
@Service
@RequiredArgsConstructor
public class NotificadorBandeja implements Notificador {

    private final NotificacionRepository repository;

    @Override
    @Transactional
    public void avisar(TipoNotificacion tipo, String titulo, String mensaje, String enlace) {
        repository.save(Notificacion.builder().tipo(tipo).titulo(titulo).mensaje(mensaje).enlace(enlace).build());
    }
}
