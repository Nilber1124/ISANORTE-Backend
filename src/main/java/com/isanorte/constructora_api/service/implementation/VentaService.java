package com.isanorte.constructora_api.service.implementation;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.isanorte.constructora_api.dto.request.AjusteStockRequest;
import com.isanorte.constructora_api.dto.request.VentaRequest;
import com.isanorte.constructora_api.dto.response.VentaResponse;
import com.isanorte.constructora_api.enums.TipoDocumentoCliente;
import com.isanorte.constructora_api.enums.TipoMovimientoStock;
import com.isanorte.constructora_api.exception.ModelNotFoundException;
import com.isanorte.constructora_api.model.DetalleVenta;
import com.isanorte.constructora_api.model.Producto;
import com.isanorte.constructora_api.model.VarianteProducto;
import com.isanorte.constructora_api.model.Venta;
import com.isanorte.constructora_api.repository.ProductoRepository;
import com.isanorte.constructora_api.repository.VentaRepository;
import com.isanorte.constructora_api.service.IStockService;
import com.isanorte.constructora_api.util.BusinessTime;

import lombok.RequiredArgsConstructor;

/**
 * Ventas registradas a mano desde el admin (WhatsApp, tienda u otro canal). Registrar una venta descuenta el stock
 * de las líneas que son productos del catálogo con control de stock; anularla lo devuelve. Nada de esto ocurre
 * fuera de esta pantalla.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VentaService {

    private static final Pattern DNI = Pattern.compile("\\d{8}");
    private static final Pattern RUC = Pattern.compile("\\d{11}");

    private final VentaRepository ventaRepository;
    private final ProductoRepository productoRepository;
    private final IStockService stockService;

    public List<VentaResponse> findAll() {
        return ventaRepository.findAllByOrderByFechaVentaDescFechaCreacionDesc().stream().map(VentaService::toResponse).toList();
    }

    public VentaResponse findById(UUID id) {
        return toResponse(find(id));
    }

    @Transactional
    public VentaResponse create(VentaRequest request, String usuario) {
        validarDocumento(request);
        Venta venta = Venta.builder().codigo(generarCodigo()).build();
        aplicar(venta, request);
        descontarStock(venta, "Venta " + venta.getCodigo(), usuario);
        return toResponse(ventaRepository.saveAndFlush(venta));
    }

    @Transactional
    public VentaResponse update(UUID id, VentaRequest request, String usuario) {
        Venta venta = find(id);
        if (Boolean.TRUE.equals(venta.getAnulada())) {
            throw new IllegalStateException("Una venta anulada no se puede editar: reactívala primero");
        }
        validarDocumento(request);
        devolverStock(venta, "Edición de la venta " + venta.getCodigo(), usuario);
        aplicar(venta, request);
        descontarStock(venta, "Venta " + venta.getCodigo() + " (editada)", usuario);
        return toResponse(ventaRepository.saveAndFlush(venta));
    }

    /** Anula la venta (devuelve su stock) o la reactiva (vuelve a descontarlo). */
    @Transactional
    public VentaResponse setAnulada(UUID id, boolean anulada, String usuario) {
        Venta venta = find(id);
        if (anulada == Boolean.TRUE.equals(venta.getAnulada())) {
            return toResponse(venta);
        }
        if (anulada) {
            devolverStock(venta, "Anulación de la venta " + venta.getCodigo(), usuario);
        } else {
            descontarStock(venta, "Reactivación de la venta " + venta.getCodigo(), usuario);
        }
        venta.setAnulada(anulada);
        return toResponse(ventaRepository.saveAndFlush(venta));
    }

    private Venta find(UUID id) {
        return ventaRepository.findById(id)
                .orElseThrow(() -> new ModelNotFoundException("Venta no encontrada con ID: " + id));
    }

    /** Copia los datos de la solicitud y rehace las líneas; el total se calcula siempre en el servidor. */
    private void aplicar(Venta venta, VentaRequest request) {
        venta.setCanal(request.canal());
        venta.setFechaVenta(request.fechaVenta());
        venta.setClienteNombre(blankToNull(request.clienteNombre()));
        venta.setClienteTelefono(blankToNull(request.clienteTelefono()));
        venta.setTipoDocumento(request.tipoDocumento());
        venta.setDocumento(blankToNull(request.documento()));
        venta.setRazonSocial(blankToNull(request.razonSocial()));
        venta.setMetodoPago(request.metodoPago());
        venta.setPagado(request.pagado());
        venta.setNotas(blankToNull(request.notas()));

        venta.getDetalles().clear();
        BigDecimal total = BigDecimal.ZERO;
        for (VentaRequest.DetalleVentaRequest item : request.detalles()) {
            Producto producto = item.productoId() == null ? null : productoRepository.findById(item.productoId())
                    .orElseThrow(() -> new ModelNotFoundException("Producto no encontrado con ID: " + item.productoId()));
            VarianteProducto variante = resolverVariante(producto, item.varianteId());
            BigDecimal subtotal = item.precioUnitario().multiply(BigDecimal.valueOf(item.cantidad()))
                    .setScale(2, RoundingMode.HALF_UP);
            total = total.add(subtotal);
            venta.addDetalle(DetalleVenta.builder()
                    .producto(producto)
                    .variante(variante)
                    .nombre(item.nombre().strip())
                    .sku(blankToNull(item.sku()))
                    .cantidad(item.cantidad())
                    .precioUnitario(item.precioUnitario().setScale(2, RoundingMode.HALF_UP))
                    .subtotal(subtotal)
                    .build());
        }
        venta.setTotal(total);
    }

    private VarianteProducto resolverVariante(Producto producto, UUID varianteId) {
        if (producto == null) {
            if (varianteId != null) {
                throw new IllegalArgumentException("Una variante necesita su producto");
            }
            return null;
        }
        if (varianteId != null) {
            return producto.getVariantes().stream()
                    .filter(v -> v.getId().equals(varianteId))
                    .findFirst()
                    .orElseThrow(() -> new ModelNotFoundException(
                            "Variante no encontrada con ID: " + varianteId + " en el producto " + producto.getId()));
        }
        boolean variantesConStock = producto.getVariantes().stream().anyMatch(v -> v.getStockActual() != null);
        if (variantesConStock && producto.getStockActual() == null) {
            throw new IllegalArgumentException("Elige el acabado de «" + producto.getNombre() + "»: su stock se controla por acabado");
        }
        return null;
    }

    /** Descuenta el stock de las líneas que controlan stock y marca la venta como descontada. */
    private void descontarStock(Venta venta, String motivo, String usuario) {
        mover(venta, TipoMovimientoStock.SALIDA, motivo, usuario);
        venta.setStockDescontado(true);
    }

    private void devolverStock(Venta venta, String motivo, String usuario) {
        if (Boolean.TRUE.equals(venta.getStockDescontado())) {
            mover(venta, TipoMovimientoStock.ENTRADA, motivo, usuario);
            venta.setStockDescontado(false);
        }
    }

    private void mover(Venta venta, TipoMovimientoStock tipo, String motivo, String usuario) {
        for (DetalleVenta detalle : venta.getDetalles()) {
            Producto producto = detalle.getProducto();
            if (producto == null) {
                continue; // un artículo fuera del catálogo no tiene stock que mover
            }
            AjusteStockRequest ajuste = new AjusteStockRequest(tipo, detalle.getCantidad(), motivo);
            VarianteProducto variante = detalle.getVariante();
            if (variante != null) {
                if (variante.getStockActual() != null) {
                    stockService.ajustarVariante(producto.getId(), variante.getId(), ajuste, usuario);
                }
            } else if (producto.getStockActual() != null) {
                stockService.ajustarProducto(producto.getId(), ajuste, usuario);
            }
        }
    }

    private static void validarDocumento(VentaRequest request) {
        TipoDocumentoCliente tipo = request.tipoDocumento();
        String documento = request.documento() == null ? "" : request.documento().strip();
        if (tipo == null && !documento.isEmpty()) {
            throw new IllegalArgumentException("Indica si el documento es DNI o RUC");
        }
        if (tipo != null && documento.isEmpty()) {
            throw new IllegalArgumentException("Escribe el número de " + tipo);
        }
        if (tipo == TipoDocumentoCliente.DNI && !DNI.matcher(documento).matches()) {
            throw new IllegalArgumentException("El DNI debe tener 8 dígitos");
        }
        if (tipo == TipoDocumentoCliente.RUC && !RUC.matcher(documento).matches()) {
            throw new IllegalArgumentException("El RUC debe tener 11 dígitos");
        }
    }

    private String generarCodigo() {
        String codigo;
        do {
            codigo = "VTA-" + BusinessTime.today().toString().replace("-", "") + "-"
                    + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        } while (ventaRepository.existsByCodigo(codigo));
        return codigo;
    }

    private static String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.strip();
    }

    private static VentaResponse toResponse(Venta v) {
        return new VentaResponse(v.getId(), v.getCodigo(), v.getCanal(), v.getFechaVenta(), v.getClienteNombre(),
                v.getClienteTelefono(), v.getTipoDocumento(), v.getDocumento(), v.getRazonSocial(), v.getMetodoPago(),
                v.getPagado(), v.getTotal(), v.getNotas(), v.getAnulada(), v.getStockDescontado(),
                v.getDetalles().stream()
                        .map(d -> new VentaResponse.DetalleVentaResponse(d.getId(),
                                d.getProducto() == null ? null : d.getProducto().getId(),
                                d.getVariante() == null ? null : d.getVariante().getId(),
                                d.getNombre(), d.getSku(), d.getCantidad(), d.getPrecioUnitario(), d.getSubtotal()))
                        .toList(),
                v.getFechaCreacion());
    }
}
