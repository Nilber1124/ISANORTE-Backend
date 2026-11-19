package com.isanorte.constructora_api.repository;

import java.util.List;
import java.util.UUID;

import com.isanorte.constructora_api.model.IsadecorSeccionExcluido;

public interface IsadecorSeccionExcluidoRepository extends IGenericRepository<IsadecorSeccionExcluido, UUID> {

    List<IsadecorSeccionExcluido> findBySeccionId(UUID seccionId);

    void deleteBySeccionId(UUID seccionId);
}
