package com.isanorte.constructora_api.service.implementation;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.isanorte.constructora_api.dto.request.AjusteStockRequest;
import com.isanorte.constructora_api.dto.response.MovimientoStockResponse;
import com.isanorte.constructora_api.dto.response.StockResponse;
import com.isanorte.constructora_api.enums.TipoMovimientoStock;
import com.isanorte.constructora_api.exception.ModelNotFoundException;
import com.isanorte.constructora_api.model.MovimientoStock;
import com.isanorte.constructora_api.model.Producto;
import com.isanorte.constructora_api.model.VarianteProducto;
import com.isanorte.constructora_api.repository.MovimientoStockRepository;
import com.isanorte.constructora_api.repository.ProductoRepository;
import com.isanorte.constructora_api.service.IStockService;
import com.isanorte.constructora_api.util.DisponibilidadStock;

import lombok.RequiredArgsConstructor;

/** Gestión manual del stock de productos y acabados, con historial de movimientos. */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StockService implements IStockService {

    private final ProductoRepository productoRepository;
    private final MovimientoStockRepository movimientoRepository;

    @Override
    @Transactional
    public StockResponse ajustarProducto(UUID productoId, AjusteStockRequest request, String usuario) {
        Producto producto = findProducto(productoId);
        int nuevo = calcular(producto.getStockActual(), request);
        producto.setStockActual(nuevo);
        DisponibilidadStock.recalcular(producto);
        productoRepository.saveAndFlush(producto);
        registrar(producto, null, request, nuevo, usuario);
        return respuesta(producto, null, nuevo);
    }

    @Override
    @Transactional
    public StockResponse ajustarVariante(UUID productoId, UUID varianteId, AjusteStockRequest request, String usuario) {
        Producto producto = findProducto(productoId);
        VarianteProducto variante = producto.getVariantes().stream()
                .filter(item -> item.getId().equals(varianteId))
                .findFirst()
                .orElseThrow(() -> new ModelNotFoundException(
                        "Variante no encontrada con ID: " + varianteId + " en el producto " + productoId));
        int nuevo = calcular(variante.getStockActual(), request);
        variante.setStockActual(nuevo);
        variante.setDisponible(nuevo > 0);
        DisponibilidadStock.recalcular(producto);
        productoRepository.saveAndFlush(producto);
        registrar(producto, variante, request, nuevo, usuario);
        return respuesta(producto, variante, nuevo);
    }

    @Override
    public List<MovimientoStockResponse> historial(UUID productoId) {
        if (!productoRepository.existsById(productoId)) {
            throw new ModelNotFoundException("Producto no encontrado con ID: " + productoId);
        }
        return movimientoRepository.findByProductoIdOrderByFechaDescIdDesc(productoId).stream()
                .map(m -> new MovimientoStockResponse(
                        m.getId(), m.getVariante() == null ? null : m.getVariante().getId(), m.getTipo(), m.getCantidad(),
                        m.getStockResultante(), m.getMotivo(), m.getPedidoId(), m.getUsuario(), m.getFecha()))
                .toList();
    }

    private Producto findProducto(UUID id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new ModelNotFoundException("Producto no encontrado con ID: " + id));
    }

    private int calcular(Integer actual, AjusteStockRequest request) {
        TipoMovimientoStock tipo = request.tipo();
        int cantidad = request.cantidad();
        return switch (tipo) {
            case ENTRADA -> {
                exigirPositiva(cantidad);
                yield (actual == null ? 0 : actual) + cantidad;
            }
            case SALIDA -> {
                exigirPositiva(cantidad);
                if (actual == null) {
                    throw new IllegalStateException("El producto no controla stock: registra primero una entrada o un ajuste");
                }
                if (actual < cantidad) {
                    throw new IllegalArgumentException("Stock insuficiente: hay " + actual + " y se pidió retirar " + cantidad);
                }
                yield actual - cantidad;
            }
            case AJUSTE -> cantidad;
            default -> throw new IllegalArgumentException("Tipo de movimiento no permitido en un ajuste manual: " + tipo);
        };
    }

    private void exigirPositiva(int cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor que cero");
        }
    }

    private void registrar(Producto producto, VarianteProducto variante, AjusteStockRequest request, int stockResultante, String usuario) {
        movimientoRepository.save(MovimientoStock.builder()
                .producto(producto)
                .variante(variante)
                .tipo(request.tipo())
                .cantidad(request.cantidad())
                .stockResultante(stockResultante)
                .motivo(request.motivo() == null || request.motivo().isBlank() ? null : request.motivo().trim())
                .usuario(usuario)
                .build());
    }

    private StockResponse respuesta(Producto producto, VarianteProducto variante, int stock) {
        DisponibilidadStock.Resumen resumen = DisponibilidadStock.resumen(producto);
        return new StockResponse(producto.getId(), variante == null ? null : variante.getId(), stock,
                producto.getStockMinimo(), resumen.estado(), resumen.quedan());
    }
}
