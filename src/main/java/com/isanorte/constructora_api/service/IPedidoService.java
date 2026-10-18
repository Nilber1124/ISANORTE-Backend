package com.isanorte.constructora_api.service;

import java.util.List;
import java.util.UUID;

import com.isanorte.constructora_api.dto.request.EstadoPedidoRequest;
import com.isanorte.constructora_api.dto.request.PublicPedidoRequest;
import com.isanorte.constructora_api.dto.response.PedidoResponse;
import com.isanorte.constructora_api.dto.response.PublicPedidoResponse;
import com.isanorte.constructora_api.enums.EstadoPedido;
import com.isanorte.constructora_api.model.Pedido;

public interface IPedidoService extends IGenericService<Pedido, UUID> {

    Pedido findByCodigo(String codigo);

    List<Pedido> findByEstado(EstadoPedido estado);

    List<PedidoResponse> findAllResponse();

    PedidoResponse findByIdResponse(UUID id);

    List<PedidoResponse> findByEstadoResponse(EstadoPedido estado);

    PedidoResponse updateEstado(UUID id, EstadoPedidoRequest request);

    PublicPedidoResponse createPublic(String siteKey, String unitSlug, PublicPedidoRequest request, UUID clienteId);

    List<PublicPedidoResponse> findPublicByCliente(UUID clienteId);
}
