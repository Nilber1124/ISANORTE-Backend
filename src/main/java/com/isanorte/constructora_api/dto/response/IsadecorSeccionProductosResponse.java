package com.isanorte.constructora_api.dto.response;

import com.isanorte.constructora_api.enums.ClaveSeccionProductos;
import com.isanorte.constructora_api.enums.ModoSeccionProductos;
import java.time.LocalDateTime;
import java.util.UUID;

public record IsadecorSeccionProductosResponse(
        UUID id,
        UUID unidadNegocioId,
        ClaveSeccionProductos clave,
        String titulo,
        String textoEnlace,
        String categoriaEnlace,
        ModoSeccionProductos modo,
        Integer ventanaDias,
        Integer limite,
        Boolean activo,
        LocalDateTime fechaCreacion,
        LocalDateTime fechaActualizacion) {
}
