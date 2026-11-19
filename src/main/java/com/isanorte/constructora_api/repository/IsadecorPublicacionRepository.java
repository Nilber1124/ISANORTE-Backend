package com.isanorte.constructora_api.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.isanorte.constructora_api.model.IsadecorPublicacion;

@Repository
public interface IsadecorPublicacionRepository extends IGenericRepository<IsadecorPublicacion, UUID> {

    List<IsadecorPublicacion> findAllByOrderByOrdenAscFechaCreacionDescIdAsc();

    List<IsadecorPublicacion> findByUnidadNegocioIdAndActivoTrueOrderByOrdenAscFechaCreacionDescIdAsc(UUID unidadNegocioId);

    boolean existsByUnidadNegocioIdAndUrl(UUID unidadNegocioId, String url);
}
