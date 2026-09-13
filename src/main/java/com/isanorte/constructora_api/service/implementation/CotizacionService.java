package com.isanorte.constructora_api.service.implementation;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.isanorte.constructora_api.dto.request.CotizacionRequest;
import com.isanorte.constructora_api.dto.request.EstadoCotizacionRequest;
import com.isanorte.constructora_api.dto.response.CotizacionResponse;
import com.isanorte.constructora_api.enums.EstadoCotizacion;
import com.isanorte.constructora_api.exception.ModelNotFoundException;
import com.isanorte.constructora_api.mapper.CotizacionMapper;
import com.isanorte.constructora_api.model.Cotizacion;
import com.isanorte.constructora_api.model.DetalleCotizacion;
import com.isanorte.constructora_api.model.Producto;
import com.isanorte.constructora_api.model.SeguimientoCotizacion;
import com.isanorte.constructora_api.model.VarianteProducto;
import com.isanorte.constructora_api.repository.CotizacionRepository;
import com.isanorte.constructora_api.repository.IGenericRepository;
import com.isanorte.constructora_api.repository.ProductoRepository;
import com.isanorte.constructora_api.service.ICotizacionService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CotizacionService extends GenericService<Cotizacion, UUID> implements ICotizacionService {

    private final CotizacionRepository cotizacionRepository;
    private final ProductoRepository productoRepository;
    private final CotizacionMapper cotizacionMapper;

    @Override
    protected IGenericRepository<Cotizacion, UUID> getRepo() {
        return cotizacionRepository;
    }

    @Override
    public Cotizacion findByCodigo(String codigo) {
        Cotizacion cotizacion = cotizacionRepository.findByCodigo(codigo)
                .orElseThrow(() -> new ModelNotFoundException("Cotización no encontrada con código: " + codigo));
        initializeForResponse(cotizacion);
        return cotizacion;
    }

    @Override
    public List<Cotizacion> findByEstado(EstadoCotizacion estado) {
        List<Cotizacion> cotizaciones = cotizacionRepository.findByEstado(estado);
        cotizaciones.forEach(this::initializeForResponse);
        return cotizaciones;
    }

    @Override
    @Transactional
    public Cotizacion create(CotizacionRequest request) {
        Cotizacion cotizacion = cotizacionMapper.toEntity(request);
        cotizacion.setCodigo(generateCodigo());
        cotizacion.setEstado(EstadoCotizacion.NUEVA);

        BigDecimal total = BigDecimal.ZERO;
        boolean precioIncompleto = false;
        for (CotizacionRequest.DetalleRequest detalleRequest : request.detalles()) {
            Producto producto = productoRepository.findById(detalleRequest.productoId())
                    .orElseThrow(() -> new ModelNotFoundException(
                            "Producto no encontrado con ID: " + detalleRequest.productoId()));
            VarianteProducto variante = resolveVariante(producto, detalleRequest.varianteId());
            BigDecimal precio = variante != null && variante.getPrecio() != null
                    ? variante.getPrecio()
                    : producto.getPrecioBase();
            BigDecimal subtotal = precio == null ? null : precio.multiply(BigDecimal.valueOf(detalleRequest.cantidad()));
            if (subtotal == null) {
                precioIncompleto = true;
            } else {
                total = total.add(subtotal);
            }
            DetalleCotizacion detalle = DetalleCotizacion.builder()
                    .producto(producto)
                    .variante(variante)
                    .nombreProducto(variante == null ? producto.getNombre() : producto.getNombre() + " - " + variante.getNombre())
                    .sku(variante == null ? producto.getSku() : variante.getSku())
                    .cantidad(detalleRequest.cantidad())
                    .precioUnitario(precio)
                    .subtotal(subtotal)
                    .notas(detalleRequest.notas())
                    .build();
            cotizacion.addDetalle(detalle);
        }
        cotizacion.setTotalEstimado(precioIncompleto ? null : total);
        return cotizacionRepository.save(cotizacion);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Cotizacion> findAll() {
        List<Cotizacion> cotizaciones = super.findAll();
        cotizaciones.forEach(this::initializeForResponse);
        return cotizaciones;
    }

    @Override
    @Transactional(readOnly = true)
    public Cotizacion findById(UUID id) {
        Cotizacion cotizacion = super.findById(id);
        initializeForResponse(cotizacion);
        return cotizacion;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CotizacionResponse> findAllResponse() {
        return super.findAll().stream().map(cotizacionMapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CotizacionResponse findByIdResponse(UUID id) {
        return cotizacionMapper.toResponse(super.findById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public CotizacionResponse findByCodigoResponse(String codigo) {
        Cotizacion cotizacion = cotizacionRepository.findByCodigo(codigo)
                .orElseThrow(() -> new ModelNotFoundException("Cotización no encontrada con código: " + codigo));
        return cotizacionMapper.toResponse(cotizacion);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CotizacionResponse> findByEstadoResponse(EstadoCotizacion estado) {
        return cotizacionRepository.findByEstado(estado).stream().map(cotizacionMapper::toResponse).toList();
    }

    @Override
    @Transactional
    public CotizacionResponse createResponse(CotizacionRequest request) {
        return cotizacionMapper.toResponse(create(request));
    }

    @Override
    @Transactional
    public CotizacionResponse updateEstado(UUID id, EstadoCotizacionRequest request) {
        Cotizacion cotizacion = super.findById(id);
        EstadoCotizacion estadoAnterior = cotizacion.getEstado();
        if (estadoAnterior != request.estado()) {
            cotizacion.setEstado(request.estado());
            SeguimientoCotizacion seguimiento = SeguimientoCotizacion.builder()
                    .estadoAnterior(estadoAnterior)
                    .estadoNuevo(request.estado())
                    .comentario("Cambio de estado administrativo")
                    .build();
            cotizacion.addSeguimiento(seguimiento);
            cotizacionRepository.saveAndFlush(cotizacion);
        }
        return cotizacionMapper.toResponse(cotizacion);
    }

    private VarianteProducto resolveVariante(Producto producto, UUID varianteId) {
        if (varianteId == null) {
            return null;
        }
        return producto.getVariantes().stream()
                .filter(variante -> variante.getId().equals(varianteId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "La variante indicada no pertenece al producto " + producto.getId()));
    }

    private String generateCodigo() {
        return "COT-" + LocalDate.now().toString().replace("-", "") + "-"
                + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private void initializeForResponse(Cotizacion cotizacion) {
        cotizacion.getDetalles().size();
        cotizacion.getSeguimientos().forEach(seguimiento -> {
            if (seguimiento.getAdministrador() != null) {
                seguimiento.getAdministrador().getNombre();
            }
        });
    }
}
