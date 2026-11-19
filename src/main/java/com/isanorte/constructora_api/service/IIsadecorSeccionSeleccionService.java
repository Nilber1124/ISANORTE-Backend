package com.isanorte.constructora_api.service;

import java.util.List;
import java.util.UUID;

import com.isanorte.constructora_api.dto.request.SeccionProductosIdsRequest;
import com.isanorte.constructora_api.dto.response.SeccionProductoItemResponse;

/** Productos fijados al inicio y excluidos de una sección de productos de la Home. */
public interface IIsadecorSeccionSeleccionService {

    List<SeccionProductoItemResponse> fijados(UUID seccionId);

    List<SeccionProductoItemResponse> reemplazarFijados(UUID seccionId, SeccionProductosIdsRequest request);

    List<SeccionProductoItemResponse> excluidos(UUID seccionId);

    List<SeccionProductoItemResponse> reemplazarExcluidos(UUID seccionId, SeccionProductosIdsRequest request);
}
