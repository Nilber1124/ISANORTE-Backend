package com.isanorte.constructora_api.repository;

import java.util.List;
import java.util.UUID;
import com.isanorte.constructora_api.model.IsadecorCampana;

public interface IsadecorCampanaRepository extends IGenericRepository<IsadecorCampana, UUID> {

    List<IsadecorCampana> findAllByOrderByFechaCreacionAscIdAsc();
    List<IsadecorCampana> findByUnidadNegocioIdAndActivoTrueOrderByFechaCreacionAscIdAsc(UUID unidadNegocioId);
}
