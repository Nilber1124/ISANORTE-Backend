package com.isanorte.constructora_api.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

import com.isanorte.constructora_api.enums.RobotsSeo;
import com.isanorte.constructora_api.enums.TipoPaginaSeo;

public record SeoPaginaResponse(
        UUID id, UUID configuracionSitioId, TipoPaginaSeo tipoPagina, String title,
        String description, String ogImageUrl, RobotsSeo robots, UUID unidadNegocioId,
        LocalDateTime fechaCreacion, LocalDateTime fechaActualizacion) {
}
