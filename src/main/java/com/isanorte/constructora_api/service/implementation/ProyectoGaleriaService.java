package com.isanorte.constructora_api.service.implementation;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.isanorte.constructora_api.dto.request.ActivoRequest;
import com.isanorte.constructora_api.dto.request.ProyectoGaleriaRequest;
import com.isanorte.constructora_api.dto.response.ProyectoGaleriaResponse;
import com.isanorte.constructora_api.exception.ModelNotFoundException;
import com.isanorte.constructora_api.model.ProyectoGaleria;
import com.isanorte.constructora_api.repository.ProyectoGaleriaRepository;
import com.isanorte.constructora_api.service.IProyectoGaleriaService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProyectoGaleriaService implements IProyectoGaleriaService {

    private final ProyectoGaleriaRepository repository;

    @Override
    public List<ProyectoGaleriaResponse> findAll() {
        return repository.findAllByOrderByOrdenAscIdAsc().stream().map(this::toResponse).toList();
    }

    @Override
    public ProyectoGaleriaResponse findById(UUID id) {
        return toResponse(findEntity(id));
    }

    @Override
    @Transactional
    public ProyectoGaleriaResponse create(ProyectoGaleriaRequest request) {
        ProyectoGaleria entity = new ProyectoGaleria();
        apply(request, entity);
        return toResponse(repository.save(entity));
    }

    @Override
    @Transactional
    public ProyectoGaleriaResponse update(UUID id, ProyectoGaleriaRequest request) {
        ProyectoGaleria entity = findEntity(id);
        apply(request, entity);
        return toResponse(repository.saveAndFlush(entity));
    }

    @Override
    @Transactional
    public ProyectoGaleriaResponse updateActivo(UUID id, ActivoRequest request) {
        ProyectoGaleria entity = findEntity(id);
        entity.setActivo(request.activo());
        return toResponse(repository.saveAndFlush(entity));
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        repository.delete(findEntity(id));
    }

    private ProyectoGaleria findEntity(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ModelNotFoundException("Imagen de galería no encontrado con ID: " + id));
    }

    private void apply(ProyectoGaleriaRequest request, ProyectoGaleria entity) {
        entity.setProyectoSlug(request.proyectoSlug().trim());
        entity.setUrl(request.url().trim());
        entity.setAlt(trimToNull(request.alt()));
        entity.setEtiqueta(request.etiqueta().trim());
        entity.setActivo(request.activo());
        entity.setOrden(request.orden());
    }

    private String trimToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    private ProyectoGaleriaResponse toResponse(ProyectoGaleria entity) {
        return new ProyectoGaleriaResponse(entity.getId(), entity.getProyectoSlug(), entity.getUrl(), entity.getAlt(), entity.getEtiqueta(), entity.getActivo(), entity.getOrden(),
                entity.getFechaCreacion(), entity.getFechaActualizacion());
    }
}
