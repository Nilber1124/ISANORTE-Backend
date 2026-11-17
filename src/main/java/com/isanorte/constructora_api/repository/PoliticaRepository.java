package com.isanorte.constructora_api.repository;

import java.util.List;
import java.util.UUID;

import com.isanorte.constructora_api.model.Politica;

public interface PoliticaRepository extends IGenericRepository<Politica, UUID> {

    List<Politica> findAllByOrderByOrdenAscIdAsc();

    List<Politica> findByActivoTrueOrderByOrdenAscIdAsc();

    boolean existsBySlug(String slug);

    boolean existsBySlugAndIdNot(String slug, UUID id);
}
