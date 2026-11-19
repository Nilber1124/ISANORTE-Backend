package com.isanorte.constructora_api.repository;

import java.util.List;
import java.util.UUID;

import com.isanorte.constructora_api.model.IsadecorSeccionFijado;

public interface IsadecorSeccionFijadoRepository extends IGenericRepository<IsadecorSeccionFijado, UUID> {

    List<IsadecorSeccionFijado> findBySeccionIdOrderByOrdenAscIdAsc(UUID seccionId);

    void deleteBySeccionId(UUID seccionId);
}
