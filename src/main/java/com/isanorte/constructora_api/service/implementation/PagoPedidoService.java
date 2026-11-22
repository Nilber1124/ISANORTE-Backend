package com.isanorte.constructora_api.service.implementation;

import java.util.Objects;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.isanorte.constructora_api.dto.response.PagoPedidoResponse;
import com.isanorte.constructora_api.enums.EstadoPedido;
import com.isanorte.constructora_api.enums.TipoNotificacion;
import com.isanorte.constructora_api.exception.ModelNotFoundException;
import com.isanorte.constructora_api.mapper.PedidoMapper;
import com.isanorte.constructora_api.model.Pedido;
import com.isanorte.constructora_api.model.SeguimientoPedido;
import com.isanorte.constructora_api.repository.PedidoRepository;
import com.isanorte.constructora_api.service.Notificador;
import com.isanorte.constructora_api.service.pago.PasarelaPago;
import com.isanorte.constructora_api.util.BusinessTime;

import lombok.RequiredArgsConstructor;

/** Cobra un pedido pendiente a través de la pasarela activa y actualiza su estado. */
@Service
@RequiredArgsConstructor
public class PagoPedidoService {

    private final PedidoRepository pedidoRepository;
    private final PedidoMapper pedidoMapper;
    private final PasarelaPago pasarela;
    private final Notificador notificador;

    @Transactional
    public PagoPedidoResponse pagar(UUID clienteId, String codigo, String token) {
        Pedido pedido = pedidoRepository.findByCodigo(codigo)
                .orElseThrow(() -> new ModelNotFoundException("Pedido no encontrado con código: " + codigo));
        if (!Objects.equals(clienteId, pedido.getCliente().getId())) {
            throw new ModelNotFoundException("Pedido no encontrado con código: " + codigo);
        }
        if (pedido.getEstado() != EstadoPedido.PENDIENTE_PAGO) {
            throw new IllegalStateException("Este pedido ya no está pendiente de pago");
        }
        if (pedido.getTotalPedido() == null) {
            throw new IllegalStateException("Este pedido no tiene un total definido; coordina el pago con la tienda");
        }

        PasarelaPago.ResultadoCobro resultado = pasarela.cobrar(codigo, pedido.getTotalPedido(), token);
        if (resultado.aprobado()) {
            pedido.setEstado(EstadoPedido.PAGADO_ONLINE);
            pedido.setMetodoPago("TARJETA (" + pasarela.proveedor() + ")");
            pedido.setReferenciaPago(resultado.referencia());
            pedido.setFechaPago(BusinessTime.now());
            pedido.addSeguimiento(SeguimientoPedido.builder()
                    .estadoAnterior(EstadoPedido.PENDIENTE_PAGO)
                    .estadoNuevo(EstadoPedido.PAGADO_ONLINE)
                    .comentario("Pago online aprobado (" + pasarela.proveedor() + ", ref. " + resultado.referencia() + ")")
                    .build());
            notificador.avisar(TipoNotificacion.PAGO_APROBADO, "Pago recibido: " + codigo,
                    "Se cobró S/ " + pedido.getTotalPedido() + " del pedido " + codigo + ". Ya puedes prepararlo.",
                    "/admin/pedidos");
        } else {
            notificador.avisar(TipoNotificacion.PAGO_RECHAZADO, "Pago rechazado: " + codigo,
                    "El cliente intentó pagar el pedido " + codigo + " y la pasarela lo rechazó.", "/admin/pedidos");
        }
        Pedido guardado = pedidoRepository.saveAndFlush(pedido);
        guardado.getDetalles().size();
        return new PagoPedidoResponse(resultado.aprobado(), resultado.mensaje(), pedidoMapper.toPublicResponse(guardado));
    }
}
