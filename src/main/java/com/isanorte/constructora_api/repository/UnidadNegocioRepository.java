package com.isanorte.constructora_api.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.isanorte.constructora_api.model.UnidadNegocio;

@Repository
public interface UnidadNegocioRepository extends IGenericRepository<UnidadNegocio, UUID> {

    Optional<UnidadNegocio> findBySlug(String slug);

    List<UnidadNegocio> findByActivoTrueOrderByOrdenAsc();
}
