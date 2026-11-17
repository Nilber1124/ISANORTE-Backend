package com.isanorte.constructora_api.repository;

import java.util.List;
import java.util.UUID;

import com.isanorte.constructora_api.model.Garantia;

public interface GarantiaRepository extends IGenericRepository<Garantia, UUID> {

    List<Garantia> findAllByOrderByOrdenAscIdAsc();

    List<Garantia> findByActivoTrueOrderByOrdenAscIdAsc();
}
