package com.isanorte.constructora_api.dto.response;

public record PagoPedidoResponse(boolean aprobado, String mensaje, PublicPedidoResponse pedido) {
}
