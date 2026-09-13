package com.isanorte.constructora_api.dto.request;

import com.isanorte.constructora_api.enums.EstadoCotizacion;

import jakarta.validation.constraints.NotNull;

public record EstadoCotizacionRequest(@NotNull EstadoCotizacion estado) {
}
