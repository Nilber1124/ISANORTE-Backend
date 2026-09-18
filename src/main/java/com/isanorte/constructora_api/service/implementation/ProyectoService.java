package com.isanorte.constructora_api.service.implementation;

import java.util.List;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.isanorte.constructora_api.dto.request.ProyectoRequest;
import com.isanorte.constructora_api.dto.request.ActivoRequest;
import com.isanorte.constructora_api.dto.request.ProyectoUpdateRequest;
import com.isanorte.constructora_api.dto.request.ImagenProyectoRequest;
import com.isanorte.constructora_api.dto.response.ImagenProyectoResponse;
import com.isanorte.constructora_api.dto.response.ProyectoResponse;
import com.isanorte.constructora_api.exception.ModelNotFoundException;
import com.isanorte.constructora_api.mapper.ProyectoMapper;
import com.isanorte.constructora_api.model.Proyecto;
import com.isanorte.constructora_api.model.ImagenProyecto;
import com.isanorte.constructora_api.model.Servicio;
import com.isanorte.constructora_api.repository.IGenericRepository;
import com.isanorte.constructora_api.repository.ProyectoRepository;
import com.isanorte.constructora_api.repository.ImagenProyectoRepository;
import com.isanorte.constructora_api.repository.ServicioRepository;
import com.isanorte.constructora_api.service.IProyectoService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProyectoService extends GenericService<Proyecto, UUID> implements IProyectoService {

    private final ProyectoRepository proyectoRepository;
    private final ImagenProyectoRepository imagenProyectoRepository;
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

    @Override
    @Transactional(readOnly = true)
    public List<ProyectoResponse> findAllResponse() {
        return super.findAll().stream().map(proyectoMapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ProyectoResponse findByIdResponse(UUID id) {
        return proyectoMapper.toResponse(super.findById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public ProyectoResponse findBySlugResponse(String slug) {
        Proyecto proyecto = proyectoRepository.findBySlug(slug)
                .orElseThrow(() -> new ModelNotFoundException("Proyecto no encontrado con slug: " + slug));
        return proyectoMapper.toResponse(proyecto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProyectoResponse> findActiveResponses() {
        return proyectoRepository.findByActivoTrue().stream().map(proyectoMapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ProyectoResponse findActiveBySlugResponse(String slug) {
        Proyecto proyecto = proyectoRepository.findBySlugAndActivoTrue(slug)
                .orElseThrow(() -> new ModelNotFoundException("Proyecto activo no encontrado con slug: " + slug));
        return proyectoMapper.toResponse(proyecto);
    }

    @Override
    @Transactional
    public ProyectoResponse createResponse(ProyectoRequest request) {
        return proyectoMapper.toResponse(create(request));
    }

    @Override
    @Transactional
    public ProyectoResponse update(UUID id, ProyectoUpdateRequest request) {
        Proyecto proyecto = super.findById(id);
        if (proyectoRepository.existsBySlugAndIdNot(request.slug(), id)) {
            throw new IllegalStateException("Ya existe un proyecto con slug: " + request.slug());
        }

        Set<Servicio> servicios = new HashSet<>();
        for (UUID servicioId : request.servicioIds()) {
            servicios.add(servicioRepository.findById(servicioId)
                    .orElseThrow(() -> new ModelNotFoundException("Servicio no encontrado con ID: " + servicioId)));
        }

        proyectoMapper.updateEntity(request, proyecto);
        for (Servicio actual : new HashSet<>(proyecto.getServicios())) {
            if (!request.servicioIds().contains(actual.getId())) {
                proyecto.removeServicio(actual);
            }
        }
        Set<UUID> idsActuales = proyecto.getServicios().stream()
                .map(Servicio::getId)
                .collect(java.util.stream.Collectors.toSet());
        for (Servicio servicio : servicios) {
            if (!idsActuales.contains(servicio.getId())) {
                proyecto.addServicio(servicio);
            }
        }
        return proyectoMapper.toResponse(proyectoRepository.saveAndFlush(proyecto));
    }

    @Override
    @Transactional
    public ProyectoResponse updateActivo(UUID id, ActivoRequest request) {
        Proyecto proyecto = super.findById(id);
        proyecto.setActivo(request.activo());
        return proyectoMapper.toResponse(proyectoRepository.saveAndFlush(proyecto));
    }

    @Override
    @Transactional
    public ImagenProyectoResponse createImagen(UUID proyectoId, ImagenProyectoRequest request) {
        Proyecto proyecto = findProyectoParaAdministracion(proyectoId);
        ImagenProyecto imagen = proyectoMapper.toImagenEntity(request);
        proyecto.addImagen(imagen);
        imagenProyectoRepository.saveAndFlush(imagen);
        return proyectoMapper.toImagenResponse(imagen);
    }

    @Override
    @Transactional
    public ImagenProyectoResponse updateImagen(
            UUID proyectoId, UUID imagenId, ImagenProyectoRequest request) {
        Proyecto proyecto = findProyectoParaAdministracion(proyectoId);
        ImagenProyecto imagen = findImagenDelProyecto(proyecto, imagenId);
        proyectoMapper.updateImagenEntity(request, imagen);
        imagenProyectoRepository.saveAndFlush(imagen);
        return proyectoMapper.toImagenResponse(imagen);
    }

    @Override
    @Transactional
    public void deleteImagen(UUID proyectoId, UUID imagenId) {
        Proyecto proyecto = findProyectoParaAdministracion(proyectoId);
        proyecto.removeImagen(findImagenDelProyecto(proyecto, imagenId));
        proyectoRepository.flush();
    }

    private Proyecto findProyectoParaAdministracion(UUID proyectoId) {
        return proyectoRepository.findById(proyectoId)
                .orElseThrow(() -> new ModelNotFoundException("Proyecto no encontrado con ID: " + proyectoId));
    }

    private ImagenProyecto findImagenDelProyecto(Proyecto proyecto, UUID imagenId) {
        ImagenProyecto imagen = imagenProyectoRepository.findById(imagenId)
                .orElseThrow(() -> new ModelNotFoundException("Imagen no encontrada con ID: " + imagenId));
        if (imagen.getProyecto() == null || !Objects.equals(proyecto.getId(), imagen.getProyecto().getId())) {
            throw new IllegalArgumentException(
                    "La imagen " + imagenId + " no pertenece al proyecto " + proyecto.getId());
        }
        return imagen;
    }

    private void initializeForResponse(Proyecto proyecto) {
        proyecto.getServicios().size();
        proyecto.getImagenes().size();
    }
}
