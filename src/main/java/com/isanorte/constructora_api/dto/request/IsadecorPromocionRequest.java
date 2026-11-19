package com.isanorte.constructora_api.dto.request;

import com.isanorte.constructora_api.enums.TipoPromocion;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.util.UUID;

public record IsadecorPromocionRequest(
        @NotNull UUID unidadNegocioId,
        @NotNull UUID productoId,
        @NotNull TipoPromocion tipo,
        @NotBlank @Size(max = 140) String titulo,
        @NotBlank @Size(max = 500) @Pattern(regexp = "^https?://.*") String imagenUrl,
        LocalDate fechaFin,
        @NotNull @Min(0) Integer orden,
        @NotNull Boolean principal,
        @NotNull Boolean activo) {
}
