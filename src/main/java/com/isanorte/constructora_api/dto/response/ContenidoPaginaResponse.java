package com.isanorte.constructora_api.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.isanorte.constructora_api.enums.TipoPaginaPublica;

public record ContenidoPaginaResponse(
        UUID id, UUID configuracionSitioId, TipoPaginaPublica pagina, String eyebrow, String titulo,
        String introduccion, String descripcion, String imagenUrl, String imagenAlt,
        String imagenFondoUrl, Boolean activo, List<String> tags,
        LocalDateTime fechaCreacion, LocalDateTime fechaActualizacion) {
}
