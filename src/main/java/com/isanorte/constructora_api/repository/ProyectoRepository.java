package com.isanorte.constructora_api.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.isanorte.constructora_api.model.Proyecto;

@Repository
public interface ProyectoRepository extends IGenericRepository<Proyecto, UUID> {

    Optional<Proyecto> findBySlug(String slug);

    List<Proyecto> findByActivoTrue();

    List<Proyecto> findByActivoTrueAndDestacadoTrueOrderByOrdenAscIdAsc();

    Optional<Proyecto> findBySlugAndActivoTrue(String slug);

    boolean existsBySlug(String slug);

    boolean existsBySlugAndIdNot(String slug, UUID id);
}
