package com.isanorte.constructora_api.service.implementation;

import com.isanorte.constructora_api.dto.request.ActivoRequest;
import com.isanorte.constructora_api.dto.request.IsadecorServicioEmpresaRequest;
import com.isanorte.constructora_api.dto.response.IsadecorServicioEmpresaResponse;
import com.isanorte.constructora_api.exception.ModelNotFoundException;
import com.isanorte.constructora_api.model.IsadecorServicioEmpresa;
import com.isanorte.constructora_api.model.UnidadNegocio;
import com.isanorte.constructora_api.repository.IsadecorServicioEmpresaRepository;
import com.isanorte.constructora_api.repository.UnidadNegocioRepository;
import com.isanorte.constructora_api.service.IIsadecorServicioEmpresaService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class IsadecorServicioEmpresaService implements IIsadecorServicioEmpresaService {

    private final IsadecorServicioEmpresaRepository repository;
    private final UnidadNegocioRepository unidadRepository;

    @Override
    public List<IsadecorServicioEmpresaResponse> findAll() {
        return repository.findAllByOrderByFechaCreacionAscIdAsc().stream().map(this::toResponse).toList();
    }

    @Override
    public IsadecorServicioEmpresaResponse findById(UUID id) {
        return toResponse(findEntity(id));
    }

    @Override
    @Transactional
    public IsadecorServicioEmpresaResponse create(IsadecorServicioEmpresaRequest request) {
        UnidadNegocio unidad = unidadRepository.findById(request.unidadNegocioId())
                .orElseThrow(() -> new ModelNotFoundException("Unidad de negocio no encontrada con ID: " + request.unidadNegocioId()));
        if (repository.existsByUnidadNegocioId(unidad.getId())) {
            throw new IllegalStateException("La unidad de negocio ya tiene bloque de servicios para empresas");
        }
        IsadecorServicioEmpresa entity = new IsadecorServicioEmpresa();
        apply(request, entity, unidad);
        IsadecorServicioEmpresa saved = repository.save(entity);
        return toResponse(saved);
    }

    @Override
    @Transactional
    public IsadecorServicioEmpresaResponse update(UUID id, IsadecorServicioEmpresaRequest request) {
        IsadecorServicioEmpresa entity = findEntity(id);
        UnidadNegocio unidad = unidadRepository.findById(request.unidadNegocioId())
                .orElseThrow(() -> new ModelNotFoundException("Unidad de negocio no encontrada con ID: " + request.unidadNegocioId()));
        if (!entity.getUnidadNegocio().getId().equals(unidad.getId()) && repository.existsByUnidadNegocioId(unidad.getId())) {
            throw new IllegalStateException("La unidad de negocio ya tiene bloque de servicios para empresas");
        }
        apply(request, entity, unidad);
        return toResponse(repository.saveAndFlush(entity));
    }

    @Override
    @Transactional
    public IsadecorServicioEmpresaResponse updateActivo(UUID id, ActivoRequest request) {
        IsadecorServicioEmpresa entity = findEntity(id);
        entity.setActivo(request.activo());
        return toResponse(repository.saveAndFlush(entity));
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        repository.delete(findEntity(id));
    }

    private IsadecorServicioEmpresa findEntity(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ModelNotFoundException("Bloque de servicios para empresas no encontrado con ID: " + id));
    }

    private void apply(IsadecorServicioEmpresaRequest request, IsadecorServicioEmpresa entity, UnidadNegocio unidad) {
        entity.setUnidadNegocio(unidad);
        entity.setEtiqueta(request.etiqueta().trim());
        entity.setTitulo(request.titulo().trim());
        entity.setDescripcion(request.descripcion().trim());
        entity.setTextoBoton(request.textoBoton().trim());
        entity.setEnlace(request.enlace().trim());
        entity.setActivo(request.activo());
    }

    private IsadecorServicioEmpresaResponse toResponse(IsadecorServicioEmpresa e) {
        return new IsadecorServicioEmpresaResponse(e.getId(), e.getUnidadNegocio().getId(), e.getEtiqueta(), e.getTitulo(), e.getDescripcion(), e.getTextoBoton(), e.getEnlace(), e.getActivo(), e.getFechaCreacion(), e.getFechaActualizacion());
    }
}
