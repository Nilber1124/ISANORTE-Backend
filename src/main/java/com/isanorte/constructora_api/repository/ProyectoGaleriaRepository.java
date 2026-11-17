package com.isanorte.constructora_api.repository;

import java.util.List;
import java.util.UUID;

import com.isanorte.constructora_api.model.ProyectoGaleria;

public interface ProyectoGaleriaRepository extends IGenericRepository<ProyectoGaleria, UUID> {

    List<ProyectoGaleria> findAllByOrderByOrdenAscIdAsc();

    List<ProyectoGaleria> findByActivoTrueOrderByOrdenAscIdAsc();
}
