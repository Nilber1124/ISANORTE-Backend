package com.isanorte.constructora_api.service.implementation;

import java.util.List;
import java.util.UUID;

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
        unidadNegocioMapper.updateEntity(request, unidad);
        return unidadNegocioMapper.toResponse(unidadNegocioRepository.saveAndFlush(unidad));
    }

    @Override
    @Transactional
    public UnidadNegocioResponse updateActivo(UUID id, ActivoRequest request) {
        UnidadNegocio unidad = super.findById(id);
        unidad.setActivo(request.activo());
        return unidadNegocioMapper.toResponse(unidadNegocioRepository.saveAndFlush(unidad));
    }

    private void initializeForResponse(UnidadNegocio unidad) {
        unidad.getEmpresa().getNombreComercial();
    }
}
