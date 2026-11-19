package com.isanorte.constructora_api.dto.request;

import java.util.List;
import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/** Lista ordenada de productos de una sección (fijados) o sin orden relevante (excluidos). */
public record SeccionProductosIdsRequest(@NotNull @Size(max = 50) List<@NotNull UUID> productoIds) {
}
