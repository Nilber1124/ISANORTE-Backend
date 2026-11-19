package com.isanorte.constructora_api.repository;

import java.util.List;
import java.util.UUID;
import com.isanorte.constructora_api.model.IsadecorPromocion;

public interface IsadecorPromocionRepository extends IGenericRepository<IsadecorPromocion, UUID> {

    List<IsadecorPromocion> findAllByOrderByOrdenAscIdAsc();
    List<IsadecorPromocion> findByUnidadNegocioIdAndActivoTrueOrderByOrdenAscIdAsc(UUID unidadNegocioId);
    List<IsadecorPromocion> findByUnidadNegocioIdAndPrincipalTrue(UUID unidadNegocioId);
}
