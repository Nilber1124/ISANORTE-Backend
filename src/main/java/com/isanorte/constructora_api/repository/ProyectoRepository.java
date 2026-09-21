package com.isanorte.constructora_api.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.isanorte.constructora_api.model.Proyecto;

@Repository
public interface ProyectoRepository extends IGenericRepository<Proyecto, UUID> {

    Optional<Proyecto> findBySlug(String slug);

    List<Proyecto> findByActivoTrue();

    @Query("select distinct proyecto from Proyecto proyecto left join fetch proyecto.imagenes "
            + "where proyecto.activo = true order by proyecto.orden asc, proyecto.id asc")
    List<Proyecto> findPublicActiveWithImagesOrderByOrdenAscIdAsc();

    @Query("select distinct proyecto from Proyecto proyecto left join fetch proyecto.servicios "
            + "where proyecto.id in :projectIds")
    List<Proyecto> findWithServicesByIdIn(@Param("projectIds") List<UUID> projectIds);

    List<Proyecto> findByActivoTrueAndDestacadoTrueOrderByOrdenAscIdAsc();

    Optional<Proyecto> findBySlugAndActivoTrue(String slug);

    boolean existsBySlug(String slug);

    boolean existsBySlugAndIdNot(String slug, UUID id);
}
