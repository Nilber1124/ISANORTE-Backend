package com.isanorte.constructora_api.service;

import java.util.List;
import java.util.UUID;

import com.isanorte.constructora_api.dto.request.CotizacionRequest;
import com.isanorte.constructora_api.dto.request.EstadoCotizacionRequest;
import com.isanorte.constructora_api.dto.response.CotizacionResponse;
import com.isanorte.constructora_api.enums.EstadoCotizacion;
import com.isanorte.constructora_api.model.Cotizacion;

import com.isanorte.constructora_api.dto.request.PublicCotizacionRequest;
import com.isanorte.constructora_api.dto.response.PublicCotizacionResponse;

public interface ICotizacionService extends IGenericService<Cotizacion, UUID> {

    Cotizacion findByCodigo(String codigo);

    List<Cotizacion> findByEstado(EstadoCotizacion estado);

    Cotizacion create(CotizacionRequest request);

    List<CotizacionResponse> findAllResponse();

    CotizacionResponse findByIdResponse(UUID id);

    CotizacionResponse findByCodigoResponse(String codigo);

    List<CotizacionResponse> findByEstadoResponse(EstadoCotizacion estado);

    CotizacionResponse createResponse(CotizacionRequest request);

    PublicCotizacionResponse createPublic(String siteKey, String unitSlug, PublicCotizacionRequest request);

    CotizacionResponse updateEstado(UUID id, EstadoCotizacionRequest request);
}
