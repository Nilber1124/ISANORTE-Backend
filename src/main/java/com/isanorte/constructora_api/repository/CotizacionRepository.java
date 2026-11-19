package com.isanorte.constructora_api.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.isanorte.constructora_api.enums.EstadoCotizacion;
import com.isanorte.constructora_api.model.Cotizacion;

@Repository
public interface CotizacionRepository extends IGenericRepository<Cotizacion, UUID> {

    /** Unidades cotizadas por producto desde una fecha, sin contar cotizaciones canceladas. */
    @org.springframework.data.jpa.repository.Query("""
            select d.producto.id as productoId, sum(d.cantidad) as cantidad
            from DetalleCotizacion d join d.cotizacion c
            where c.fechaCreacion >= :desde and c.estado <> com.isanorte.constructora_api.enums.EstadoCotizacion.CANCELADA
            group by d.producto.id""")
    List<com.isanorte.constructora_api.repository.projection.DemandaProducto> demandaDesde(
            @org.springframework.data.repository.query.Param("desde") java.time.LocalDateTime desde);

    Optional<Cotizacion> findByCodigo(String codigo);

    List<Cotizacion> findByEstado(EstadoCotizacion estado);

    List<Cotizacion> findByClienteIdOrderByFechaCreacionDesc(UUID clienteId);
}
