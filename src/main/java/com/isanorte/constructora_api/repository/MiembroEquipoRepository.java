package com.isanorte.constructora_api.repository;

import java.util.List;
import java.util.UUID;

import com.isanorte.constructora_api.model.MiembroEquipo;

public interface MiembroEquipoRepository extends IGenericRepository<MiembroEquipo, UUID> {

    List<MiembroEquipo> findAllByOrderByOrdenAscIdAsc();

    List<MiembroEquipo> findByActivoTrueOrderByOrdenAscIdAsc();
}
