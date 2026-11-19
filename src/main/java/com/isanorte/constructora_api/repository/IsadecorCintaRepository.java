package com.isanorte.constructora_api.repository;

import java.util.List;
import java.util.UUID;
import com.isanorte.constructora_api.model.IsadecorCinta;

public interface IsadecorCintaRepository extends IGenericRepository<IsadecorCinta, UUID> {

    List<IsadecorCinta> findAllByOrderByOrdenAscIdAsc();
    List<IsadecorCinta> findByUnidadNegocioIdAndActivoTrueOrderByOrdenAscIdAsc(UUID unidadNegocioId);
}
