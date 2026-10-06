package com.isanorte.constructora_api.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.isanorte.constructora_api.model.ResenaProducto;

@Repository
public interface ResenaProductoRepository extends IGenericRepository<ResenaProducto, UUID> {

    List<ResenaProducto> findByProductoId(UUID productoId);

    long countByProductoId(UUID productoId);

    @Query("select avg(r.calificacion) from ResenaProducto r where r.producto.id = :productoId")
    Double averageByProductoId(@Param("productoId") UUID productoId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update ResenaProducto r set r.cantidadUtil = r.cantidadUtil + 1 where r.id = :id")
    int incrementUsefulVote(@Param("id") UUID id);
}
