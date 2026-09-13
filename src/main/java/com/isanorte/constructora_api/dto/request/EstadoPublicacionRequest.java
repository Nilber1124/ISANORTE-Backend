package com.isanorte.constructora_api.dto.request;

import com.isanorte.constructora_api.enums.EstadoPublicacion;

import jakarta.validation.constraints.NotNull;

public record EstadoPublicacionRequest(@NotNull EstadoPublicacion estado) {
}
