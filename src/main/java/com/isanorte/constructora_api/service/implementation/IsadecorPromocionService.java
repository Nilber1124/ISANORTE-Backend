package com.isanorte.constructora_api.service.implementation;

import com.isanorte.constructora_api.dto.request.ActivoRequest;
import com.isanorte.constructora_api.dto.request.IsadecorPromocionRequest;
import com.isanorte.constructora_api.dto.response.IsadecorPromocionResponse;
import com.isanorte.constructora_api.exception.ModelNotFoundException;
import com.isanorte.constructora_api.model.IsadecorPromocion;
import com.isanorte.constructora_api.model.Producto;
import com.isanorte.constructora_api.model.UnidadNegocio;
import com.isanorte.constructora_api.repository.IsadecorPromocionRepository;
import com.isanorte.constructora_api.repository.ProductoRepository;
import com.isanorte.constructora_api.repository.UnidadNegocioRepository;
import com.isanorte.constructora_api.service.IIsadecorPromocionService;
import com.isanorte.constructora_api.util.BusinessTime;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class IsadecorPromocionService implements IIsadecorPromocionService {

    private final IsadecorPromocionRepository repository;
    private final UnidadNegocioRepository unidadRepository;
    private final ProductoRepository productoRepository;

    @Override
    public List<IsadecorPromocionResponse> findAll() {
        return repository.findAllByOrderByOrdenAscIdAsc().stream().map(this::toResponse).toList();
    }

    @Override
    public IsadecorPromocionResponse findById(UUID id) {
        return toResponse(findEntity(id));
    }

    @Override
    @Transactional
    public IsadecorPromocionResponse create(IsadecorPromocionRequest request) {
        UnidadNegocio unidad = unidadRepository.findById(request.unidadNegocioId())
                .orElseThrow(() -> new ModelNotFoundException("Unidad de negocio no encontrada con ID: " + request.unidadNegocioId()));
        Producto producto = productoRepository.findById(request.productoId())
                .orElseThrow(() -> new ModelNotFoundException("Producto no encontrado con ID: " + request.productoId()));
        validarFecha(request.fechaFin());
        IsadecorPromocion entity = new IsadecorPromocion();
        apply(request, entity, unidad, producto);
        IsadecorPromocion saved = repository.save(entity);
        return toResponse(saved);
    }

    @Override
    @Transactional
    public IsadecorPromocionResponse update(UUID id, IsadecorPromocionRequest request) {
        IsadecorPromocion entity = findEntity(id);
        UnidadNegocio unidad = unidadRepository.findById(request.unidadNegocioId())
                .orElseThrow(() -> new ModelNotFoundException("Unidad de negocio no encontrada con ID: " + request.unidadNegocioId()));
        Producto producto = productoRepository.findById(request.productoId())
                .orElseThrow(() -> new ModelNotFoundException("Producto no encontrado con ID: " + request.productoId()));
        validarFecha(request.fechaFin());
        apply(request, entity, unidad, producto);
        return toResponse(repository.saveAndFlush(entity));
    }

    @Override
    @Transactional
    public IsadecorPromocionResponse updateActivo(UUID id, ActivoRequest request) {
        IsadecorPromocion entity = findEntity(id);
        entity.setActivo(request.activo());
        return toResponse(repository.saveAndFlush(entity));
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        repository.delete(findEntity(id));
    }

    private IsadecorPromocion findEntity(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ModelNotFoundException("Promoción no encontrado con ID: " + id));
    }

    private void apply(IsadecorPromocionRequest request, IsadecorPromocion entity, UnidadNegocio unidad, Producto producto) {
        entity.setUnidadNegocio(unidad);
        entity.setProducto(producto);
        entity.setTipo(request.tipo());
        entity.setTitulo(request.titulo().trim());
        entity.setImagenUrl(request.imagenUrl().trim());
        entity.setFechaFin(request.fechaFin());
        entity.setOrden(request.orden());
        entity.setPrincipal(request.principal());
        entity.setActivo(request.activo());
        if (Boolean.TRUE.equals(request.principal())) {
            repository.findByUnidadNegocioIdAndPrincipalTrue(unidad.getId()).stream()
                    .filter(otra -> !otra.equals(entity))
                    .forEach(otra -> otra.setPrincipal(false));
        }
    }

    private void validarFecha(LocalDate fechaFin) {
        if (fechaFin != null && fechaFin.isBefore(BusinessTime.today())) {
            throw new IllegalArgumentException("La fecha de fin no puede estar en el pasado");
        }
    }

    private IsadecorPromocionResponse toResponse(IsadecorPromocion e) {
        return new IsadecorPromocionResponse(e.getId(), e.getUnidadNegocio().getId(), e.getProducto().getId(), e.getTipo(), e.getTitulo(), e.getImagenUrl(), e.getFechaFin(), e.getOrden(), e.getPrincipal(), e.getActivo(), e.getFechaCreacion(), e.getFechaActualizacion());
    }
}
