package com.isanorte.constructora_api.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.isanorte.constructora_api.model.Venta;

public interface VentaRepository extends JpaRepository<Venta, UUID> {

    @EntityGraph(attributePaths = "detalles")
    List<Venta> findAllByOrderByFechaVentaDescFechaCreacionDesc();

    boolean existsByCodigo(String codigo);
}
