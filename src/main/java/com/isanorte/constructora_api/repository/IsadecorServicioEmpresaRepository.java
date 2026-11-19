package com.isanorte.constructora_api.repository;

import java.util.List;
import java.util.UUID;
import com.isanorte.constructora_api.model.IsadecorServicioEmpresa;

public interface IsadecorServicioEmpresaRepository extends IGenericRepository<IsadecorServicioEmpresa, UUID> {

    List<IsadecorServicioEmpresa> findAllByOrderByFechaCreacionAscIdAsc();
    List<IsadecorServicioEmpresa> findByUnidadNegocioIdAndActivoTrueOrderByFechaCreacionAscIdAsc(UUID unidadNegocioId);
    boolean existsByUnidadNegocioId(UUID unidadNegocioId);
}
