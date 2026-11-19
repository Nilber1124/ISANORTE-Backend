package com.isanorte.constructora_api.repository;

import java.util.List;
import java.util.UUID;
import com.isanorte.constructora_api.model.IsadecorSeccionProductos;
import java.util.Optional;
import com.isanorte.constructora_api.enums.ClaveSeccionProductos;

public interface IsadecorSeccionProductosRepository extends IGenericRepository<IsadecorSeccionProductos, UUID> {

    List<IsadecorSeccionProductos> findAllByOrderByFechaCreacionAscIdAsc();
    List<IsadecorSeccionProductos> findByUnidadNegocioIdAndActivoTrueOrderByFechaCreacionAscIdAsc(UUID unidadNegocioId);
    boolean existsByUnidadNegocioIdAndClave(UUID unidadNegocioId, ClaveSeccionProductos clave);
    Optional<IsadecorSeccionProductos> findByUnidadNegocioIdAndClaveAndActivoTrue(UUID unidadNegocioId, ClaveSeccionProductos clave);
}
