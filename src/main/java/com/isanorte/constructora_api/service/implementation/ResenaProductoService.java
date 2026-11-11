package com.isanorte.constructora_api.service.implementation;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.access.AccessDeniedException;

import com.isanorte.constructora_api.dto.request.CrearResenaRequest;
import com.isanorte.constructora_api.dto.response.ResenaProductoResponse;
import com.isanorte.constructora_api.dto.response.ResumenResenasResponse;
import com.isanorte.constructora_api.enums.EstadoPublicacion;
import com.isanorte.constructora_api.enums.OrdenResena;
import com.isanorte.constructora_api.exception.ModelNotFoundException;
import com.isanorte.constructora_api.model.Producto;
import com.isanorte.constructora_api.model.ResenaProducto;
import com.isanorte.constructora_api.repository.ConfiguracionSitioRepository;
import com.isanorte.constructora_api.repository.ClienteRepository;
import com.isanorte.constructora_api.repository.ProductoRepository;
import com.isanorte.constructora_api.repository.ResenaProductoRepository;
import com.isanorte.constructora_api.repository.UnidadNegocioRepository;
import com.isanorte.constructora_api.service.IResenaProductoService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ResenaProductoService implements IResenaProductoService {
    private final ConfiguracionSitioRepository configuracionRepository;
    private final UnidadNegocioRepository unidadRepository;
    private final ProductoRepository productoRepository;
    private final ResenaProductoRepository resenaRepository;
    private final ClienteRepository clienteRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ResenaProductoResponse> findPublicReviews(
            String clave, String unidadSlug, String productoSlug, OrdenResena orden) {
        Producto producto = findPublicProduct(clave, unidadSlug, productoSlug);
        return resenaRepository.findByProductoId(producto.getId()).stream()
                .sorted(comparator(orden))
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ResumenResenasResponse findPublicSummary(String clave, String unidadSlug, String productoSlug) {
        Producto producto = findPublicProduct(clave, unidadSlug, productoSlug);
        List<ResenaProducto> resenas = resenaRepository.findByProductoId(producto.getId());
        long total = resenas.size();
        BigDecimal promedio = total == 0
                ? BigDecimal.ZERO.setScale(1)
                : BigDecimal.valueOf(resenas.stream().mapToInt(ResenaProducto::getCalificacion).average().orElse(0))
                        .setScale(1, RoundingMode.HALF_UP);
        List<ResumenResenasResponse.Distribucion> distribucion = java.util.stream.IntStream
                .iterate(5, value -> value >= 1, value -> value - 1)
                .mapToObj(calificacion -> {
                    long cantidad = resenas.stream()
                            .filter(resena -> resena.getCalificacion() == calificacion)
                            .count();
                    BigDecimal porcentaje = total == 0
                            ? BigDecimal.ZERO.setScale(1)
                            : BigDecimal.valueOf(cantidad * 100.0 / total).setScale(1, RoundingMode.HALF_UP);
                    return new ResumenResenasResponse.Distribucion(calificacion, cantidad, porcentaje);
                })
                .toList();
        return new ResumenResenasResponse(promedio, total, distribucion);
    }

    @Override
    @Transactional
    public ResenaProductoResponse createPublicReview(
            String clave, String unidadSlug, String productoSlug,
            UUID clienteId, CrearResenaRequest request) {
        Producto producto = findPublicProduct(clave, unidadSlug, productoSlug);
        var cliente = clienteRepository.findById(clienteId)
                .filter(value -> Boolean.TRUE.equals(value.getActivo()))
                .orElseThrow(() -> new AccessDeniedException("La cuenta de cliente no está activa"));
        String nombreCliente = java.util.stream.Stream.of(cliente.getNombre(), cliente.getApellido())
                .filter(value -> value != null && !value.isBlank())
                .map(String::trim)
                .collect(java.util.stream.Collectors.joining(" "));
        ResenaProducto resena = ResenaProducto.builder()
                .producto(producto)
                .clienteId(clienteId)
                .nombreCliente(nombreCliente.trim())
                .calificacion(request.calificacion())
                .titulo(normalizeOptional(request.titulo()))
                .comentario(request.comentario().trim())
                .compraVerificada(false)
                .build();
        return toResponse(resenaRepository.saveAndFlush(resena));
    }

    @Override
    @Transactional
    public ResenaProductoResponse markUseful(UUID resenaId) {
        if (resenaRepository.incrementUsefulVote(resenaId) == 0) {
            throw new ModelNotFoundException("Reseña no encontrada con id: " + resenaId);
        }
        ResenaProducto resena = resenaRepository.findById(resenaId)
                .orElseThrow(() -> new ModelNotFoundException("Reseña no encontrada con id: " + resenaId));
        return toResponse(resena);
    }

    private Producto findPublicProduct(String clave, String unidadSlug, String productoSlug) {
        var site = configuracionRepository.findByClave(clave)
                .orElseThrow(() -> new ModelNotFoundException("Sitio público no encontrado con clave: " + clave));
        var unit = unidadRepository.findByEmpresaIdAndSlugAndActivoTrue(site.getEmpresa().getId(), unidadSlug)
                .orElseThrow(() -> new ModelNotFoundException(
                        "Unidad pública activa no encontrada con slug: " + unidadSlug));
        return productoRepository.findByUnidadNegocioIdAndSlugAndEstado(
                        unit.getId(), productoSlug, EstadoPublicacion.PUBLICADO)
                .orElseThrow(() -> new ModelNotFoundException(
                        "Producto público no encontrado con slug: " + productoSlug));
    }

    private Comparator<ResenaProducto> comparator(OrdenResena orden) {
        Comparator<ResenaProducto> recent = Comparator.comparing(ResenaProducto::getFechaCreacion).reversed();
        return switch (orden) {
            case MAYOR_CALIFICACION -> Comparator.comparingInt(ResenaProducto::getCalificacion).reversed()
                    .thenComparing(recent);
            case MENOR_CALIFICACION -> Comparator.comparingInt(ResenaProducto::getCalificacion)
                    .thenComparing(recent);
            case MAS_UTILES -> Comparator.comparingInt(ResenaProducto::getCantidadUtil).reversed()
                    .thenComparing(recent);
            case RECIENTES -> recent;
        };
    }

    private ResenaProductoResponse toResponse(ResenaProducto resena) {
        return new ResenaProductoResponse(
                resena.getId(), resena.getNombreCliente(), resena.getCalificacion(), resena.getTitulo(),
                resena.getComentario(), resena.getFechaCreacion(), Boolean.TRUE.equals(resena.getCompraVerificada()),
                resena.getCantidadUtil());
    }

    private String normalizeOptional(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
