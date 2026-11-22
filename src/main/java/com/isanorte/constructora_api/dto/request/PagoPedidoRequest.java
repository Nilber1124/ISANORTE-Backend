package com.isanorte.constructora_api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Token del cobro generado por la pasarela en el navegador (en modo simulado: APROBADO o RECHAZADO). */
public record PagoPedidoRequest(@NotBlank @Size(max = 200) String token) {
}
