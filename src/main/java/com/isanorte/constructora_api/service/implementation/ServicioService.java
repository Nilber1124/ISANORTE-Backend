package com.isanorte.constructora_api.service.implementation;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.isanorte.constructora_api.dto.request.ActivoRequest;
import com.isanorte.constructora_api.dto.request.ServicioRequest;
import com.isanorte.constructora_api.dto.request.ServicioUpdateRequest;
import com.isanorte.constructora_api.dto.response.ServicioResponse;
import com.isanorte.constructora_api.exception.ModelNotFoundException;
import com.isanorte.constructora_api.mapper.ServicioMapper;
import com.isanorte.constructora_api.model.Servicio;
import com.isanorte.constructora_api.repository.IGenericRepository;
import com.isanorte.constructora_api.repository.ServicioRepository;
import com.isanorte.constructora_api.service.IServicioService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ServicioService extends GenericService<Servicio, UUID> implements IServicioService {

    private final ServicioRepository servicioRepository;
    private final ServicioMapper servicioMapper;

    @Override
    protected IGenericRepository<Servicio, UUID> getRepo() {
        return servicioRepository;
    }

    @Override
    public Servicio findBySlug(String slug) {
        return servicioRepository.findBySlug(slug)
                .orElseThrow(() -> new ModelNotFoundException("Servicio no encontrado con slug: " + slug));
    }

    @Override
    @Transactional
    public Servicio create(ServicioRequest request) {
        if (servicioRepository.existsBySlug(request.slug())) {
            throw new IllegalStateException("Ya existe un servicio con slug: " + request.slug());
        }
        return servicioRepository.save(servicioMapper.toEntity(request));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ServicioResponse> findAllResponse() {
        return super.findAll().stream().map(servicioMapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ServicioResponse findByIdResponse(UUID id) {
        return servicioMapper.toResponse(super.findById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public ServicioResponse findBySlugResponse(String slug) {
        Servicio servicio = servicioRepository.findBySlug(slug)
                .orElseThrow(() -> new ModelNotFoundException("Servicio no encontrado con slug: " + slug));
        return servicioMapper.toResponse(servicio);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ServicioResponse> findActiveResponses() {
        return servicioRepository.findByActivoTrueOrderByOrdenAsc().stream()
                .map(servicioMapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ServicioResponse findActiveBySlugResponse(String slug) {
        Servicio servicio = servicioRepository.findBySlugAndActivoTrue(slug)
                .orElseThrow(() -> new ModelNotFoundException("Servicio activo no encontrado con slug: " + slug));
        return servicioMapper.toResponse(servicio);
    }

    @Override
    @Transactional
    public ServicioResponse createResponse(ServicioRequest request) {
        return servicioMapper.toResponse(create(request));
    }

    @Override
    @Transactional
    public ServicioResponse update(UUID id, ServicioUpdateRequest request) {
        Servicio servicio = super.findById(id);
        if (servicioRepository.existsBySlugAndIdNot(request.slug(), id)) {
            throw new IllegalStateException("Ya existe un servicio con slug: " + request.slug());
        }
        servicioMapper.updateEntity(request, servicio);
        return servicioMapper.toResponse(servicioRepository.saveAndFlush(servicio));
    }

    @Override
    @Transactional
    public ServicioResponse updateActivo(UUID id, ActivoRequest request) {
        Servicio servicio = super.findById(id);
        servicio.setActivo(request.activo());
        return servicioMapper.toResponse(servicioRepository.saveAndFlush(servicio));
    }
}
