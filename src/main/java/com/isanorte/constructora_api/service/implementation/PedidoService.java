package com.isanorte.constructora_api.service.implementation;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.isanorte.constructora_api.dto.request.EstadoPedidoRequest;
import com.isanorte.constructora_api.dto.request.PublicPedidoRequest;
import com.isanorte.constructora_api.dto.response.PedidoResponse;
import com.isanorte.constructora_api.dto.response.PublicPedidoResponse;
import com.isanorte.constructora_api.enums.EstadoDisponibilidad;
import com.isanorte.constructora_api.enums.EstadoPedido;
import com.isanorte.constructora_api.enums.EstadoPublicacion;
import com.isanorte.constructora_api.exception.ModelNotFoundException;
import com.isanorte.constructora_api.mapper.PedidoMapper;
import com.isanorte.constructora_api.model.Cliente;
import com.isanorte.constructora_api.model.ConfiguracionSitio;
import com.isanorte.constructora_api.model.DetallePedido;
import com.isanorte.constructora_api.model.DireccionCliente;
import com.isanorte.constructora_api.model.Pedido;
import com.isanorte.constructora_api.model.Producto;
import com.isanorte.constructora_api.model.SeguimientoPedido;
import com.isanorte.constructora_api.model.UnidadNegocio;
import com.isanorte.constructora_api.model.VarianteProducto;
import com.isanorte.constructora_api.repository.ClienteRepository;
import com.isanorte.constructora_api.repository.ConfiguracionSitioRepository;
import com.isanorte.constructora_api.repository.DireccionClienteRepository;
import com.isanorte.constructora_api.repository.IGenericRepository;
import com.isanorte.constructora_api.repository.PedidoRepository;
import com.isanorte.constructora_api.repository.ProductoRepository;
import com.isanorte.constructora_api.repository.UnidadNegocioRepository;
import com.isanorte.constructora_api.service.IPedidoService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PedidoService extends GenericService<Pedido, UUID> implements IPedidoService {

    private static final int MAX_REINTENTOS_STOCK = 2;

    private final PedidoRepository pedidoRepository;
    private final ProductoRepository productoRepository;
    private final ConfiguracionSitioRepository configuracionRepository;
    private final UnidadNegocioRepository unidadRepository;
    private final ClienteRepository clienteRepository;
    private final DireccionClienteRepository direccionRepository;
    private final PedidoMapper pedidoMapper;

    /**
     * Referencia a sí mismo a través del proxy de Spring (ObjectProvider ya es perezoso, no requiere
     * {@code @Lazy}): necesaria para que el reintento ante un choque de concurrencia (ver
     * {@link #createPublic}) abra una transacción nueva en cada intento en vez de reutilizar la
     * transacción ya marcada para rollback.
     */
    private final ObjectProvider<PedidoService> self;

    @Override
    protected IGenericRepository<Pedido, UUID> getRepo() {
        return pedidoRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Pedido findByCodigo(String codigo) {
        Pedido pedido = pedidoRepository.findByCodigo(codigo)
                .orElseThrow(() -> new ModelNotFoundException("Pedido no encontrado con código: " + codigo));
        initializeForResponse(pedido);
        return pedido;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Pedido> findByEstado(EstadoPedido estado) {
        List<Pedido> pedidos = pedidoRepository.findByEstado(estado);
        pedidos.forEach(this::initializeForResponse);
        return pedidos;
    }

    @Override
    @Transactional(readOnly = true)
    public List<PedidoResponse> findAllResponse() {
        return super.findAll().stream().map(pedidoMapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public PedidoResponse findByIdResponse(UUID id) {
        return pedidoMapper.toResponse(super.findById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<PedidoResponse> findByEstadoResponse(EstadoPedido estado) {
        return pedidoRepository.findByEstado(estado).stream().map(pedidoMapper::toResponse).toList();
    }

    @Override
    @Transactional
    public PedidoResponse updateEstado(UUID id, EstadoPedidoRequest request) {
        Pedido pedido = super.findById(id);
        EstadoPedido estadoAnterior = pedido.getEstado();
        EstadoPedido estadoNuevo = request.estado();

        if (estadoAnterior == estadoNuevo) {
            return pedidoMapper.toResponse(pedido);
        }
        if (estadoAnterior == EstadoPedido.CANCELADO) {
            throw new IllegalStateException("Un pedido cancelado no puede cambiar de estado");
        }

        if (estadoNuevo == EstadoPedido.CANCELADO && Boolean.TRUE.equals(pedido.getStockDescontado())) {
            for (DetallePedido detalle : pedido.getDetalles()) {
                Producto producto = detalle.getProducto();
                if (producto.getStockActual() != null) {
                    producto.setStockActual(producto.getStockActual() + detalle.getCantidad());
                }
            }
            pedido.setStockDescontado(false);
        }

        pedido.setEstado(estadoNuevo);
        pedido.addSeguimiento(SeguimientoPedido.builder()
                .estadoAnterior(estadoAnterior)
                .estadoNuevo(estadoNuevo)
                .comentario("Cambio de estado administrativo")
                .build());
        return pedidoMapper.toResponse(pedidoRepository.saveAndFlush(pedido));
    }

    /**
     * Entrada pública: reintenta el checkout completo (no solo el descuento de stock) si choca con
     * otra compra concurrente del mismo producto, hasta {@value #MAX_REINTENTOS_STOCK} veces.
     * Cada intento corre en su propia transacción nueva (vía el proxy de Spring, nunca self-invocación
     * directa) porque una transacción que falló por bloqueo optimista no es segura para reutilizar.
     * {@code NOT_SUPPORTED} es necesario: sin él, este método heredaría el
     * {@code @Transactional(readOnly = true)} de nivel de clase de {@link GenericService}, y
     * {@code crearPedidoTransaccional} se uniría a esa transacción de solo lectura en vez de abrir
     * una propia, haciendo fallar el INSERT real del pedido.
     */
    @Override
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public PublicPedidoResponse createPublic(
            String siteKey, String unitSlug, PublicPedidoRequest request, UUID clienteId) {
        int intento = 0;
        while (true) {
            try {
                return self.getObject().crearPedidoTransaccional(siteKey, unitSlug, request, clienteId);
            } catch (ObjectOptimisticLockingFailureException exception) {
                intento++;
                if (intento > MAX_REINTENTOS_STOCK) {
                    throw new IllegalStateException(
                            "Uno de los productos se agotó mientras completabas tu compra. Actualiza tu carrito e inténtalo nuevamente.");
                }
            }
        }
    }

    @Transactional
    public PublicPedidoResponse crearPedidoTransaccional(
            String siteKey, String unitSlug, PublicPedidoRequest request, UUID clienteId) {
        ConfiguracionSitio site = configuracionRepository.findByClave(siteKey)
                .orElseThrow(() -> new ModelNotFoundException("Sitio público no encontrado con clave: " + siteKey));
        UnidadNegocio unit = unidadRepository.findByEmpresaIdAndSlugAndActivoTrue(site.getEmpresa().getId(), unitSlug)
                .orElseThrow(() -> new ModelNotFoundException("Unidad pública activa no encontrada con slug: " + unitSlug));
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new ModelNotFoundException("Cuenta de cliente no encontrada"));

        if (request.detalles() == null || request.detalles().isEmpty()) {
            throw new IllegalArgumentException("El pedido debe incluir al menos un producto");
        }

        Pedido pedido = Pedido.builder()
                .cliente(cliente)
                .codigo(generateCodigo())
                .estado(EstadoPedido.PENDIENTE_PAGO)
                .ruc(blankToNull(request.ruc()))
                .razonSocial(blankToNull(request.razonSocial()))
                .build();
        aplicarDireccion(pedido, request, clienteId);

        BigDecimal total = BigDecimal.ZERO;
        boolean precioIncompleto = false;
        for (PublicPedidoRequest.DetallePublicoRequest item : request.detalles()) {
            if (item.productoSlug() == null || item.productoSlug().isBlank()) {
                throw new IllegalArgumentException("El slug del producto no puede estar vacío");
            }
            Producto producto = productoRepository.findByUnidadNegocioIdAndSlugAndEstado(
                    unit.getId(), item.productoSlug().trim(), EstadoPublicacion.PUBLICADO)
                    .orElseThrow(() -> new ModelNotFoundException(
                            "Producto público no encontrado con slug: " + item.productoSlug().trim()
                                    + " en la unidad " + unitSlug));

            VarianteProducto variante = resolveVariante(producto, item.varianteSku());
            int cantidad = item.cantidad() != null && item.cantidad() > 0 ? item.cantidad() : 1;

            descontarStock(producto, cantidad);

            BigDecimal precio = variante != null && variante.getPrecio() != null
                    ? variante.getPrecio()
                    : producto.getPrecioBase();
            BigDecimal subtotal = precio == null ? null : precio.multiply(BigDecimal.valueOf(cantidad));
            if (subtotal == null) {
                precioIncompleto = true;
            } else {
                total = total.add(subtotal);
            }

            DetallePedido detalle = DetallePedido.builder()
                    .producto(producto)
                    .variante(variante)
                    .nombreProducto(variante == null ? producto.getNombre() : producto.getNombre() + " - " + variante.getNombre())
                    .sku(variante == null ? producto.getSku() : variante.getSku())
                    .cantidad(cantidad)
                    .precioUnitario(precio)
                    .subtotal(subtotal)
                    .build();
            pedido.addDetalle(detalle);
        }

        pedido.setTotalPedido(precioIncompleto ? null : total);
        pedido.setStockDescontado(true);
        return pedidoMapper.toPublicResponse(pedidoRepository.saveAndFlush(pedido));
    }

    @Override
    @Transactional(readOnly = true)
    public List<PublicPedidoResponse> findPublicByCliente(UUID clienteId) {
        return pedidoRepository.findByClienteIdOrderByFechaCreacionDesc(clienteId).stream()
                .map(pedidoMapper::toPublicResponse)
                .toList();
    }

    /** Toma la dirección de una dirección guardada (verificando que sea del cliente) o de los campos en línea del request. */
    private void aplicarDireccion(Pedido pedido, PublicPedidoRequest request, UUID clienteId) {
        if (request.direccionId() != null) {
            DireccionCliente direccion = direccionRepository.findById(request.direccionId())
                    .orElseThrow(() -> new ModelNotFoundException(
                            "Dirección no encontrada con ID: " + request.direccionId()));
            if (!Objects.equals(clienteId, direccion.getCliente().getId())) {
                throw new IllegalArgumentException("La dirección no pertenece a la cuenta indicada");
            }
            pedido.setDireccionAlias(direccion.getAlias());
            pedido.setDireccionCalle(direccion.getCalle());
            pedido.setDireccionDistrito(direccion.getDistrito());
            pedido.setDireccionCiudad(direccion.getCiudad());
            pedido.setDireccionReferencia(direccion.getReferencia());
            pedido.setDireccionTelefonoContacto(direccion.getTelefonoContacto());
            return;
        }

        if (isBlank(request.direccionCalle()) || isBlank(request.direccionDistrito())
                || isBlank(request.direccionCiudad()) || isBlank(request.direccionTelefonoContacto())) {
            throw new IllegalArgumentException(
                    "Indica una dirección guardada (direccionId) o completa calle, distrito, ciudad y teléfono de contacto");
        }
        pedido.setDireccionAlias(blankToNull(request.direccionAlias()));
        pedido.setDireccionCalle(request.direccionCalle().trim());
        pedido.setDireccionDistrito(request.direccionDistrito().trim());
        pedido.setDireccionCiudad(request.direccionCiudad().trim());
        pedido.setDireccionReferencia(blankToNull(request.direccionReferencia()));
        pedido.setDireccionTelefonoContacto(request.direccionTelefonoContacto().trim());
    }

    /** Descuenta stock si el producto gestiona inventario (stockActual no nulo); lanza si no alcanza. */
    private void descontarStock(Producto producto, int cantidad) {
        if (producto.getStockActual() == null) {
            return;
        }
        if (producto.getStockActual() < cantidad) {
            throw new IllegalStateException(
                    "No hay stock suficiente de " + producto.getNombre() + " para completar el pedido");
        }
        producto.setStockActual(producto.getStockActual() - cantidad);
        if (producto.getStockActual() <= 0) {
            producto.setDisponibilidad(EstadoDisponibilidad.AGOTADO);
        }
        productoRepository.saveAndFlush(producto);
    }

    private VarianteProducto resolveVariante(Producto producto, String varianteSku) {
        if (varianteSku == null || varianteSku.isBlank()) {
            return null;
        }
        String skuToFind = varianteSku.trim();
        return producto.getVariantes().stream()
                .filter(variante -> variante.getSku() != null && variante.getSku().equalsIgnoreCase(skuToFind))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "La variante con SKU '" + varianteSku + "' no pertenece al producto " + producto.getNombre()));
    }

    private String generateCodigo() {
        return "PED-" + LocalDate.now().toString().replace("-", "") + "-"
                + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private String blankToNull(String value) {
        return isBlank(value) ? null : value.trim();
    }

    private void initializeForResponse(Pedido pedido) {
        pedido.getDetalles().size();
        pedido.getSeguimientos().forEach(seguimiento -> {
            if (seguimiento.getAdministrador() != null) {
                seguimiento.getAdministrador().getNombre();
            }
        });
    }
}
