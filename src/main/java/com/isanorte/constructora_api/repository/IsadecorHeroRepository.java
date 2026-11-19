package com.isanorte.constructora_api.repository;

import java.util.List;
import java.util.UUID;
import com.isanorte.constructora_api.model.IsadecorHero;

public interface IsadecorHeroRepository extends IGenericRepository<IsadecorHero, UUID> {

    List<IsadecorHero> findAllByOrderByFechaCreacionAscIdAsc();
    List<IsadecorHero> findByUnidadNegocioIdAndActivoTrueOrderByFechaCreacionAscIdAsc(UUID unidadNegocioId);
    boolean existsByUnidadNegocioId(UUID unidadNegocioId);
}
