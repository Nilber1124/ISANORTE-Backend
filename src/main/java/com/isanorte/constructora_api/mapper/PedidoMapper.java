package com.isanorte.constructora_api.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import com.isanorte.constructora_api.dto.response.PedidoResponse;
import com.isanorte.constructora_api.dto.response.PublicPedidoResponse;
import com.isanorte.constructora_api.model.DetallePedido;
import com.isanorte.constructora_api.model.Pedido;
import com.isanorte.constructora_api.model.SeguimientoPedido;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface PedidoMapper {

    @Mapping(target = "clienteId", source = "cliente.id")
    PedidoResponse toResponse(Pedido pedido);

    @Mapping(target = "productoId", source = "producto.id")
    @Mapping(target = "varianteId", source = "variante.id")
    PedidoResponse.DetalleResponse toDetalleResponse(DetallePedido detalle);

    @Mapping(target = "administradorId", source = "administrador.id")
    @Mapping(target = "administradorNombre", source = "administrador.nombre")
    PedidoResponse.SeguimientoResponse toSeguimientoResponse(SeguimientoPedido seguimiento);

    PublicPedidoResponse toPublicResponse(Pedido pedido);

    @Mapping(target = "productoSlug", source = "producto.slug")
    @Mapping(target = "varianteSku", source = "variante.sku")
    PublicPedidoResponse.DetallePublicoResponse toDetallePublicoResponse(DetallePedido detalle);
}
