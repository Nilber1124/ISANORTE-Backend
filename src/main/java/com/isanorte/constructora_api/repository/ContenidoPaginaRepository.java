package com.isanorte.constructora_api.repository;

import java.util.UUID;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.isanorte.constructora_api.model.ContenidoPagina;

@Repository
public interface ContenidoPaginaRepository extends IGenericRepository<ContenidoPagina, UUID> {
    boolean existsByConfiguracionSitioIdAndPagina(UUID configuracionSitioId,
            com.isanorte.constructora_api.enums.TipoPaginaPublica pagina);

    Optional<ContenidoPagina> findByConfiguracionSitioIdAndPaginaAndActivoTrue(UUID configuracionSitioId,
            com.isanorte.constructora_api.enums.TipoPaginaPublica pagina);
}
