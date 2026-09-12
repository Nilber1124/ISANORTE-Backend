package com.isanorte.constructora_api.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.isanorte.constructora_api.enums.EstadoCotizacion;
import com.isanorte.constructora_api.model.Cotizacion;

@Repository
public interface CotizacionRepository extends IGenericRepository<Cotizacion, UUID> {

    Optional<Cotizacion> findByCodigo(String codigo);

    List<Cotizacion> findByEstado(EstadoCotizacion estado);
}
