package com.isanorte.constructora_api.dto.response;

import java.util.List;

public record BandejaNotificacionesResponse(long noLeidas, List<NotificacionResponse> items) {
}
