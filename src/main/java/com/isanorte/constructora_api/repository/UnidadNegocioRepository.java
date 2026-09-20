package com.isanorte.constructora_api.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.isanorte.constructora_api.model.UnidadNegocio;

@Repository
public interface UnidadNegocioRepository extends IGenericRepository<UnidadNegocio, UUID> {

    Optional<UnidadNegocio> findBySlug(String slug);

    Optional<UnidadNegocio> findBySlugAndActivoTrue(String slug);

    List<UnidadNegocio> findByActivoTrueOrderByOrdenAsc();

    boolean existsBySlugAndIdNot(String slug, UUID id);

    boolean existsByNombreAndIdNot(String nombre, UUID id);

    boolean existsByEmpresaIdAndActivoTrueAndDestacadoTrue(UUID empresaId);

    boolean existsByEmpresaIdAndActivoTrueAndDestacadoTrueAndIdNot(UUID empresaId, UUID id);

    Optional<UnidadNegocio> findByEmpresaIdAndActivoTrueAndDestacadoTrue(UUID empresaId);

    Optional<UnidadNegocio> findByEmpresaIdAndSlugAndActivoTrue(UUID empresaId, String slug);

    List<UnidadNegocio> findByEmpresaIdAndActivoTrueOrderByOrdenAscIdAsc(UUID empresaId);
}
