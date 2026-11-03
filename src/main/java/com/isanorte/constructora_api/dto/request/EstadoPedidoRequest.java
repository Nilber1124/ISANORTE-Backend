package com.isanorte.constructora_api.dto.request;

import com.isanorte.constructora_api.enums.EstadoPedido;

import jakarta.validation.constraints.NotNull;

public record EstadoPedidoRequest(@NotNull EstadoPedido estado) {
}
