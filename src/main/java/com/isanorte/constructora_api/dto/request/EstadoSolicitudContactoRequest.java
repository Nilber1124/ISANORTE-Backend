package com.isanorte.constructora_api.dto.request;

import com.isanorte.constructora_api.enums.EstadoSolicitudContacto;

import jakarta.validation.constraints.NotNull;

public record EstadoSolicitudContactoRequest(@NotNull EstadoSolicitudContacto estado) {
}
