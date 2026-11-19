package com.isanorte.constructora_api.dto.request;

import com.isanorte.constructora_api.enums.ClaveSeccionProductos;
import com.isanorte.constructora_api.enums.ModoSeccionProductos;
import jakarta.validation.constraints.*;
import java.util.UUID;

public record IsadecorSeccionProductosRequest(
        @NotNull UUID unidadNegocioId,
        @NotNull ClaveSeccionProductos clave,
        @NotBlank @Size(max = 120) String titulo,
        @NotBlank @Size(max = 80) String textoEnlace,
        @Size(max = 150) String categoriaEnlace,
        @NotNull ModoSeccionProductos modo,
        @NotNull @Min(1) @Max(365) Integer ventanaDias,
        @NotNull @Min(1) @Max(24) Integer limite,
        @NotNull Boolean activo) {
}
