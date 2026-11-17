package com.isanorte.constructora_api.repository;

import java.util.List;
import java.util.UUID;

import com.isanorte.constructora_api.model.PasoProceso;

public interface PasoProcesoRepository extends IGenericRepository<PasoProceso, UUID> {

    List<PasoProceso> findAllByOrderByOrdenAscIdAsc();

    List<PasoProceso> findByActivoTrueOrderByOrdenAscIdAsc();
}
