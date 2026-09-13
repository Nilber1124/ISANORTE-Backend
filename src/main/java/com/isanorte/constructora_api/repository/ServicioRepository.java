package com.isanorte.constructora_api.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.isanorte.constructora_api.model.Servicio;

@Repository
public interface ServicioRepository extends IGenericRepository<Servicio, UUID> {

    Optional<Servicio> findBySlug(String slug);

    List<Servicio> findByActivoTrueOrderByOrdenAsc();

    Optional<Servicio> findBySlugAndActivoTrue(String slug);

    boolean existsBySlug(String slug);

    boolean existsBySlugAndIdNot(String slug, UUID id);
}
