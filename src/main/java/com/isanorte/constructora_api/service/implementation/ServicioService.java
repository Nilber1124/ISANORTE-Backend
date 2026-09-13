package com.isanorte.constructora_api.service.implementation;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.isanorte.constructora_api.dto.request.ServicioRequest;
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
}
