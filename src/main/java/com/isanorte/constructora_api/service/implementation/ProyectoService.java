package com.isanorte.constructora_api.service.implementation;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.isanorte.constructora_api.dto.request.ProyectoRequest;
import com.isanorte.constructora_api.exception.ModelNotFoundException;
import com.isanorte.constructora_api.mapper.ProyectoMapper;
import com.isanorte.constructora_api.model.Proyecto;
import com.isanorte.constructora_api.model.Servicio;
import com.isanorte.constructora_api.repository.IGenericRepository;
import com.isanorte.constructora_api.repository.ProyectoRepository;
import com.isanorte.constructora_api.repository.ServicioRepository;
import com.isanorte.constructora_api.service.IProyectoService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProyectoService extends GenericService<Proyecto, UUID> implements IProyectoService {

    private final ProyectoRepository proyectoRepository;
    private final ServicioRepository servicioRepository;
    private final ProyectoMapper proyectoMapper;

    @Override
    protected IGenericRepository<Proyecto, UUID> getRepo() {
        return proyectoRepository;
    }

    @Override
    public Proyecto findBySlug(String slug) {
        Proyecto proyecto = proyectoRepository.findBySlug(slug)
                .orElseThrow(() -> new ModelNotFoundException("Proyecto no encontrado con slug: " + slug));
        initializeForResponse(proyecto);
        return proyecto;
    }

    @Override
    @Transactional
    public Proyecto create(ProyectoRequest request) {
        if (proyectoRepository.existsBySlug(request.slug())) {
            throw new IllegalStateException("Ya existe un proyecto con slug: " + request.slug());
        }
        Proyecto proyecto = proyectoMapper.toEntity(request);
        if (request.imagenes() != null) {
            request.imagenes().stream()
                    .map(proyectoMapper::toImagenEntity)
                    .forEach(proyecto::addImagen);
        }
        if (request.servicioIds() != null) {
            for (UUID servicioId : request.servicioIds()) {
                Servicio servicio = servicioRepository.findById(servicioId)
                        .orElseThrow(() -> new ModelNotFoundException("Servicio no encontrado con ID: " + servicioId));
                proyecto.addServicio(servicio);
            }
        }
        return proyectoRepository.save(proyecto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Proyecto> findAll() {
        List<Proyecto> proyectos = super.findAll();
        proyectos.forEach(this::initializeForResponse);
        return proyectos;
    }

    @Override
    @Transactional(readOnly = true)
    public Proyecto findById(UUID id) {
        Proyecto proyecto = super.findById(id);
        initializeForResponse(proyecto);
        return proyecto;
    }

    private void initializeForResponse(Proyecto proyecto) {
        proyecto.getServicios().size();
        proyecto.getImagenes().size();
    }
}
