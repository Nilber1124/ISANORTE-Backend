package com.isanorte.constructora_api.service.implementation;

import java.util.List;
import java.util.UUID;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.isanorte.constructora_api.dto.request.ActivoRequest;
import com.isanorte.constructora_api.dto.request.UnidadNegocioRequest;
import com.isanorte.constructora_api.dto.request.UnidadNegocioUpdateRequest;
import com.isanorte.constructora_api.dto.response.UnidadNegocioResponse;
import com.isanorte.constructora_api.exception.ModelNotFoundException;
import com.isanorte.constructora_api.mapper.UnidadNegocioMapper;
import com.isanorte.constructora_api.model.Empresa;
import com.isanorte.constructora_api.model.UnidadNegocio;
import com.isanorte.constructora_api.model.RecursoUnidadNegocio;
import com.isanorte.constructora_api.enums.TipoRecursoUnidadNegocio;
import com.isanorte.constructora_api.dto.request.RecursoUnidadNegocioRequest;
import com.isanorte.constructora_api.dto.response.RecursoUnidadNegocioResponse;
import com.isanorte.constructora_api.mapper.DynamicContentMapper;
import com.isanorte.constructora_api.repository.RecursoUnidadNegocioRepository;
import com.isanorte.constructora_api.repository.EmpresaRepository;
import com.isanorte.constructora_api.repository.IGenericRepository;
import com.isanorte.constructora_api.repository.UnidadNegocioRepository;
import com.isanorte.constructora_api.service.IUnidadNegocioService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UnidadNegocioService extends GenericService<UnidadNegocio, UUID> implements IUnidadNegocioService {

    private final UnidadNegocioRepository unidadNegocioRepository;
    private final EmpresaRepository empresaRepository;
    private final UnidadNegocioMapper unidadNegocioMapper;
    private final DynamicContentMapper dynamicContentMapper;
    private final RecursoUnidadNegocioRepository recursoUnidadNegocioRepository;

    @Override
    protected IGenericRepository<UnidadNegocio, UUID> getRepo() {
        return unidadNegocioRepository;
    }

    @Override
    public UnidadNegocio findBySlug(String slug) {
        UnidadNegocio unidad = unidadNegocioRepository.findBySlug(slug)
                .orElseThrow(() -> new ModelNotFoundException("Unidad de negocio no encontrada con slug: " + slug));
        initializeForResponse(unidad);
        return unidad;
    }

    @Override
    public List<UnidadNegocio> findByActivoTrueOrderByOrdenAsc() {
        List<UnidadNegocio> unidades = unidadNegocioRepository.findByActivoTrueOrderByOrdenAsc();
        unidades.forEach(this::initializeForResponse);
        return unidades;
    }

    @Override
    @Transactional
    public UnidadNegocio create(UnidadNegocioRequest request) {
        if (unidadNegocioRepository.findBySlug(request.slug()).isPresent()) {
            throw new IllegalStateException("Ya existe una unidad de negocio con slug: " + request.slug());
        }
        Empresa empresa = empresaRepository.findById(request.empresaId())
                .orElseThrow(() -> new ModelNotFoundException("Empresa no encontrada con ID: " + request.empresaId()));
        UnidadNegocio unidad = unidadNegocioMapper.toEntity(request);
        validateFeatured(empresa.getId(), null, unidad.getActivo(), unidad.getDestacado());
        empresa.addUnidadNegocio(unidad);
        return unidadNegocioRepository.save(unidad);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UnidadNegocio> findAll() {
        List<UnidadNegocio> unidades = super.findAll();
        unidades.forEach(this::initializeForResponse);
        return unidades;
    }

    @Override
    @Transactional(readOnly = true)
    public UnidadNegocio findById(UUID id) {
        UnidadNegocio unidad = super.findById(id);
        initializeForResponse(unidad);
        return unidad;
    }

    @Override
    @Transactional(readOnly = true)
    public List<UnidadNegocioResponse> findAllResponse() {
        return super.findAll().stream().map(unidadNegocioMapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public UnidadNegocioResponse findByIdResponse(UUID id) {
        return unidadNegocioMapper.toResponse(super.findById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public UnidadNegocioResponse findBySlugResponse(String slug) {
        UnidadNegocio unidad = unidadNegocioRepository.findBySlug(slug)
                .orElseThrow(() -> new ModelNotFoundException("Unidad de negocio no encontrada con slug: " + slug));
        return unidadNegocioMapper.toResponse(unidad);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UnidadNegocioResponse> findActiveResponses() {
        return unidadNegocioRepository.findByActivoTrueOrderByOrdenAsc().stream()
                .map(unidadNegocioMapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public UnidadNegocioResponse findActiveBySlugResponse(String slug) {
        UnidadNegocio unidad = unidadNegocioRepository.findBySlugAndActivoTrue(slug)
                .orElseThrow(() -> new ModelNotFoundException(
                        "Unidad de negocio activa no encontrada con slug: " + slug));
        return unidadNegocioMapper.toResponse(unidad);
    }

    @Override
    @Transactional
    public UnidadNegocioResponse createResponse(UnidadNegocioRequest request) {
        return unidadNegocioMapper.toResponse(create(request));
    }

    @Override
    @Transactional
    public UnidadNegocioResponse update(UUID id, UnidadNegocioUpdateRequest request) {
        UnidadNegocio unidad = super.findById(id);
        if (unidadNegocioRepository.existsBySlugAndIdNot(request.slug(), id)) {
            throw new IllegalStateException("Ya existe una unidad de negocio con slug: " + request.slug());
        }
        if (unidadNegocioRepository.existsByNombreAndIdNot(request.nombre(), id)) {
            throw new IllegalStateException("Ya existe una unidad de negocio con nombre: " + request.nombre());
        }
        Empresa empresa = empresaRepository.findById(request.empresaId())
                .orElseThrow(() -> new ModelNotFoundException("Empresa no encontrada con ID: " + request.empresaId()));
        if (!unidad.getEmpresa().getId().equals(empresa.getId())) {
            throw new IllegalStateException("No se permite cambiar la empresa de una unidad de negocio existente");
        }
        validateFeatured(empresa.getId(), id, request.activo(), request.destacado());
        unidadNegocioMapper.updateEntity(request, unidad);
        return unidadNegocioMapper.toResponse(unidadNegocioRepository.saveAndFlush(unidad));
    }

    @Override
    @Transactional
    public UnidadNegocioResponse updateActivo(UUID id, ActivoRequest request) {
        UnidadNegocio unidad = super.findById(id);
        validateFeatured(unidad.getEmpresa().getId(), id, request.activo(), unidad.getDestacado());
        unidad.setActivo(request.activo());
        return unidadNegocioMapper.toResponse(unidadNegocioRepository.saveAndFlush(unidad));
    }

    @Override
    @Transactional
    public RecursoUnidadNegocioResponse createRecurso(UUID unidadId, RecursoUnidadNegocioRequest request) {
        UnidadNegocio unidad = findUnidadAdmin(unidadId);
        validateResource(request);
        RecursoUnidadNegocio recurso = dynamicContentMapper.toEntity(request);
        unidad.addRecurso(recurso);
        return dynamicContentMapper.toResponse(recursoUnidadNegocioRepository.saveAndFlush(recurso));
    }

    @Override
    @Transactional
    public RecursoUnidadNegocioResponse updateRecurso(
            UUID unidadId, UUID recursoId, RecursoUnidadNegocioRequest request) {
        UnidadNegocio unidad = findUnidadAdmin(unidadId);
        RecursoUnidadNegocio recurso = recursoUnidadNegocioRepository.findById(recursoId)
                .orElseThrow(() -> new ModelNotFoundException("Recurso no encontrado con ID: " + recursoId));
        requireResourceOwnership(unidad, recurso);
        validateResource(request);
        dynamicContentMapper.update(request, recurso);
        return dynamicContentMapper.toResponse(recursoUnidadNegocioRepository.saveAndFlush(recurso));
    }

    @Override
    @Transactional
    public void deleteRecurso(UUID unidadId, UUID recursoId) {
        UnidadNegocio unidad = findUnidadAdmin(unidadId);
        RecursoUnidadNegocio recurso = recursoUnidadNegocioRepository.findById(recursoId)
                .orElseThrow(() -> new ModelNotFoundException("Recurso no encontrado con ID: " + recursoId));
        requireResourceOwnership(unidad, recurso);
        unidad.removeRecurso(recurso);
        unidadNegocioRepository.flush();
    }

    private UnidadNegocio findUnidadAdmin(UUID id) {
        return unidadNegocioRepository.findById(id)
                .orElseThrow(() -> new ModelNotFoundException("Unidad de negocio no encontrada con ID: " + id));
    }

    private void requireResourceOwnership(UnidadNegocio unidad, RecursoUnidadNegocio recurso) {
        if (recurso.getUnidadNegocio() == null
                || !Objects.equals(unidad.getId(), recurso.getUnidadNegocio().getId())) {
            throw new IllegalArgumentException("El recurso no pertenece a la unidad indicada");
        }
    }

    private void validateResource(RecursoUnidadNegocioRequest request) {
        if (request.tipo() == TipoRecursoUnidadNegocio.IMAGEN_EDITORIAL
                && (request.alt() == null || request.alt().isBlank())) {
            throw new IllegalArgumentException("alt es obligatorio para una imagen editorial");
        }
    }

    private void validateFeatured(UUID empresaId, UUID currentId, Boolean active, Boolean featured) {
        if (!Boolean.TRUE.equals(active) || !Boolean.TRUE.equals(featured)) return;
        boolean exists = currentId == null
                ? unidadNegocioRepository.existsByEmpresaIdAndActivoTrueAndDestacadoTrue(empresaId)
                : unidadNegocioRepository.existsByEmpresaIdAndActivoTrueAndDestacadoTrueAndIdNot(empresaId, currentId);
        if (exists) throw new IllegalStateException("La empresa ya tiene una unidad activa y destacada");
    }

    private void initializeForResponse(UnidadNegocio unidad) {
        unidad.getEmpresa().getNombreComercial();
        unidad.getRecursos().size();
    }
}
