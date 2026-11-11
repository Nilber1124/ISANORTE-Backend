package com.isanorte.constructora_api.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.isanorte.constructora_api.enums.EstadoPedido;
import com.isanorte.constructora_api.model.Pedido;

@Repository
public interface PedidoRepository extends IGenericRepository<Pedido, UUID> {

    Optional<Pedido> findByCodigo(String codigo);

    List<Pedido> findByEstado(EstadoPedido estado);

    List<Pedido> findByClienteIdOrderByFechaCreacionDesc(UUID clienteId);
}
