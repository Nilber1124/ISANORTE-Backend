package com.isanorte.constructora_api.service.implementation;

import com.isanorte.constructora_api.dto.request.ActivoRequest;
import com.isanorte.constructora_api.dto.request.IsadecorSeccionProductosRequest;
import com.isanorte.constructora_api.dto.response.IsadecorSeccionProductosResponse;
import com.isanorte.constructora_api.exception.ModelNotFoundException;
import com.isanorte.constructora_api.model.IsadecorSeccionProductos;
import com.isanorte.constructora_api.model.UnidadNegocio;
import com.isanorte.constructora_api.repository.IsadecorSeccionProductosRepository;
import com.isanorte.constructora_api.repository.UnidadNegocioRepository;
import com.isanorte.constructora_api.service.IIsadecorSeccionProductosService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class IsadecorSeccionProductosService implements IIsadecorSeccionProductosService {

    private final IsadecorSeccionProductosRepository repository;
    private final UnidadNegocioRepository unidadRepository;

    @Override
    public List<IsadecorSeccionProductosResponse> findAll() {
        return repository.findAllByOrderByFechaCreacionAscIdAsc().stream().map(this::toResponse).toList();
    }

    @Override
    public IsadecorSeccionProductosResponse findById(UUID id) {
        return toResponse(findEntity(id));
    }

    @Override
    @Transactional
    public IsadecorSeccionProductosResponse create(IsadecorSeccionProductosRequest request) {
        UnidadNegocio unidad = unidadRepository.findById(request.unidadNegocioId())
                .orElseThrow(() -> new ModelNotFoundException("Unidad de negocio no encontrada con ID: " + request.unidadNegocioId()));
        if (repository.existsByUnidadNegocioIdAndClave(unidad.getId(), request.clave())) {
            throw new IllegalStateException("La unidad de negocio ya tiene la sección " + request.clave());
        }
        IsadecorSeccionProductos entity = new IsadecorSeccionProductos();
        apply(request, entity, unidad);
        IsadecorSeccionProductos saved = repository.save(entity);
        return toResponse(saved);
    }

    @Override
    @Transactional
    public IsadecorSeccionProductosResponse update(UUID id, IsadecorSeccionProductosRequest request) {
        IsadecorSeccionProductos entity = findEntity(id);
        UnidadNegocio unidad = unidadRepository.findById(request.unidadNegocioId())
                .orElseThrow(() -> new ModelNotFoundException("Unidad de negocio no encontrada con ID: " + request.unidadNegocioId()));
        boolean cambio = !entity.getUnidadNegocio().getId().equals(unidad.getId()) || entity.getClave() != request.clave();
        if (cambio && repository.existsByUnidadNegocioIdAndClave(unidad.getId(), request.clave())) {
            throw new IllegalStateException("La unidad de negocio ya tiene la sección " + request.clave());
        }
        apply(request, entity, unidad);
        return toResponse(repository.saveAndFlush(entity));
    }

    @Override
    @Transactional
    public IsadecorSeccionProductosResponse updateActivo(UUID id, ActivoRequest request) {
        IsadecorSeccionProductos entity = findEntity(id);
        entity.setActivo(request.activo());
        return toResponse(repository.saveAndFlush(entity));
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        repository.delete(findEntity(id));
    }

    private IsadecorSeccionProductos findEntity(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ModelNotFoundException("Sección de productos no encontrado con ID: " + id));
    }

    private void apply(IsadecorSeccionProductosRequest request, IsadecorSeccionProductos entity, UnidadNegocio unidad) {
        entity.setUnidadNegocio(unidad);
        entity.setClave(request.clave());
        entity.setTitulo(request.titulo().trim());
        entity.setTextoEnlace(request.textoEnlace().trim());
        entity.setCategoriaEnlace(trimToNull(request.categoriaEnlace()));
        entity.setModo(request.modo());
        entity.setVentanaDias(request.ventanaDias());
        entity.setLimite(request.limite());
        entity.setActivo(request.activo());
    }

    private String trimToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    private IsadecorSeccionProductosResponse toResponse(IsadecorSeccionProductos e) {
        return new IsadecorSeccionProductosResponse(e.getId(), e.getUnidadNegocio().getId(), e.getClave(), e.getTitulo(), e.getTextoEnlace(), e.getCategoriaEnlace(), e.getModo(), e.getVentanaDias(), e.getLimite(), e.getActivo(), e.getFechaCreacion(), e.getFechaActualizacion());
    }
}
