package com.isanorte.constructora_api.service;

import java.util.List;
import java.util.UUID;

import com.isanorte.constructora_api.dto.request.CrearResenaRequest;
import com.isanorte.constructora_api.dto.response.ResenaProductoResponse;
import com.isanorte.constructora_api.dto.response.ResumenResenasResponse;
import com.isanorte.constructora_api.enums.OrdenResena;

public interface IResenaProductoService {
    List<ResenaProductoResponse> findPublicReviews(
            String clave, String unidadSlug, String productoSlug, OrdenResena orden);

    ResumenResenasResponse findPublicSummary(String clave, String unidadSlug, String productoSlug);

    ResenaProductoResponse createPublicReview(
            String clave, String unidadSlug, String productoSlug,
            UUID clienteId, CrearResenaRequest request);

    ResenaProductoResponse markUseful(UUID resenaId);
}
