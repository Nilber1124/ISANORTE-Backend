package com.isanorte.constructora_api.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.isanorte.constructora_api.enums.EstadoPedido;
import com.isanorte.constructora_api.model.Pedido;

@Repository
public interface PedidoRepository extends IGenericRepository<Pedido, UUID> {

    /** Unidades pedidas por producto desde una fecha, sin contar pedidos cancelados. */
    @org.springframework.data.jpa.repository.Query("""
            select d.producto.id as productoId, sum(d.cantidad) as cantidad
            from DetallePedido d join d.pedido p
            where p.fechaCreacion >= :desde and p.estado <> com.isanorte.constructora_api.enums.EstadoPedido.CANCELADO
            group by d.producto.id""")
    List<com.isanorte.constructora_api.repository.projection.DemandaProducto> demandaDesde(
            @org.springframework.data.repository.query.Param("desde") java.time.LocalDateTime desde);

    Optional<Pedido> findByCodigo(String codigo);

    List<Pedido> findByEstado(EstadoPedido estado);

    List<Pedido> findByClienteIdOrderByFechaCreacionDesc(UUID clienteId);
}
