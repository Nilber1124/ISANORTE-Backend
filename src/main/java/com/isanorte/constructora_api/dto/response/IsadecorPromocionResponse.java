package com.isanorte.constructora_api.dto.response;

import com.isanorte.constructora_api.enums.TipoPromocion;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record IsadecorPromocionResponse(
        UUID id,
        UUID unidadNegocioId,
        UUID productoId,
        TipoPromocion tipo,
        String titulo,
        String imagenUrl,
        LocalDate fechaFin,
        Integer orden,
        Boolean principal,
        Boolean activo,
        LocalDateTime fechaCreacion,
        LocalDateTime fechaActualizacion) {
}
