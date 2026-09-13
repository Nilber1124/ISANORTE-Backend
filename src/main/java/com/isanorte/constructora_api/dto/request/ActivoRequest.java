package com.isanorte.constructora_api.dto.request;

import jakarta.validation.constraints.NotNull;

public record ActivoRequest(@NotNull Boolean activo) {
}
