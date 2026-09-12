package com.isanorte.constructora_api.service;

import java.util.List;
import java.util.UUID;

import com.isanorte.constructora_api.enums.EstadoCotizacion;
import com.isanorte.constructora_api.model.Cotizacion;

public interface ICotizacionService extends IGenericService<Cotizacion, UUID> {

    Cotizacion findByCodigo(String codigo);

    List<Cotizacion> findByEstado(EstadoCotizacion estado);
}
