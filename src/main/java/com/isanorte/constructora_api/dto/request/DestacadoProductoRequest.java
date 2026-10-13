package com.isanorte.constructora_api.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record DestacadoProductoRequest(
        @NotNull Boolean destacado,
        @NotNull @PositiveOrZero Integer ordenDestacado) {
}
