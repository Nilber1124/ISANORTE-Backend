package com.isanorte.constructora_api.repository;

import java.util.UUID;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.isanorte.constructora_api.model.SeoPagina;

@Repository
public interface SeoPaginaRepository extends IGenericRepository<SeoPagina, UUID> {
    boolean existsByConfiguracionSitioIdAndTipoPaginaAndUnidadNegocioIsNull(UUID configuracionSitioId,
            com.isanorte.constructora_api.enums.TipoPaginaSeo tipoPagina);

    boolean existsByUnidadNegocioId(UUID unidadNegocioId);

    Optional<SeoPagina> findByConfiguracionSitioIdAndTipoPaginaAndUnidadNegocioIsNull(UUID configuracionSitioId,
            com.isanorte.constructora_api.enums.TipoPaginaSeo tipoPagina);

    Optional<SeoPagina> findByConfiguracionSitioIdAndTipoPaginaAndUnidadNegocioId(UUID configuracionSitioId,
            com.isanorte.constructora_api.enums.TipoPaginaSeo tipoPagina, UUID unidadNegocioId);
}
