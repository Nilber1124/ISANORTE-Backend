package com.isanorte.constructora_api.service;

import com.isanorte.constructora_api.enums.TipoNotificacion;

/**
 * Puerto de avisos al administrador. Hoy la única implementación guarda el aviso en la bandeja del panel;
 * cuando el cliente entregue credenciales, se agrega otra (correo SMTP, Telegram, WhatsApp) sin tocar a quien avisa.
 */
public interface Notificador {

    void avisar(TipoNotificacion tipo, String titulo, String mensaje, String enlace);
}
