package com.isanorte.constructora_api.service.implementation;

import com.isanorte.constructora_api.dto.request.ActivoRequest;
import com.isanorte.constructora_api.dto.request.IsadecorHeroRequest;
import com.isanorte.constructora_api.dto.response.IsadecorHeroResponse;
import com.isanorte.constructora_api.exception.ModelNotFoundException;
import com.isanorte.constructora_api.model.IsadecorHero;
import com.isanorte.constructora_api.model.UnidadNegocio;
import com.isanorte.constructora_api.repository.IsadecorHeroRepository;
import com.isanorte.constructora_api.repository.UnidadNegocioRepository;
import com.isanorte.constructora_api.service.IIsadecorHeroService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class IsadecorHeroService implements IIsadecorHeroService {

    private final IsadecorHeroRepository repository;
    private final UnidadNegocioRepository unidadRepository;

    @Override
    public List<IsadecorHeroResponse> findAll() {
        return repository.findAllByOrderByFechaCreacionAscIdAsc().stream().map(this::toResponse).toList();
    }

    @Override
    public IsadecorHeroResponse findById(UUID id) {
        return toResponse(findEntity(id));
    }

    @Override
    @Transactional
    public IsadecorHeroResponse create(IsadecorHeroRequest request) {
        UnidadNegocio unidad = unidadRepository.findById(request.unidadNegocioId())
                .orElseThrow(() -> new ModelNotFoundException("Unidad de negocio no encontrada con ID: " + request.unidadNegocioId()));
        if (repository.existsByUnidadNegocioId(unidad.getId())) {
            throw new IllegalStateException("La unidad de negocio ya tiene hero");
        }
        IsadecorHero entity = new IsadecorHero();
        apply(request, entity, unidad);
        IsadecorHero saved = repository.save(entity);
        return toResponse(saved);
    }

    @Override
    @Transactional
    public IsadecorHeroResponse update(UUID id, IsadecorHeroRequest request) {
        IsadecorHero entity = findEntity(id);
        UnidadNegocio unidad = unidadRepository.findById(request.unidadNegocioId())
                .orElseThrow(() -> new ModelNotFoundException("Unidad de negocio no encontrada con ID: " + request.unidadNegocioId()));
        if (!entity.getUnidadNegocio().getId().equals(unidad.getId()) && repository.existsByUnidadNegocioId(unidad.getId())) {
            throw new IllegalStateException("La unidad de negocio ya tiene hero");
        }
        apply(request, entity, unidad);
        return toResponse(repository.saveAndFlush(entity));
    }

    @Override
    @Transactional
    public IsadecorHeroResponse updateActivo(UUID id, ActivoRequest request) {
        IsadecorHero entity = findEntity(id);
        entity.setActivo(request.activo());
        return toResponse(repository.saveAndFlush(entity));
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        repository.delete(findEntity(id));
    }

    private IsadecorHero findEntity(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ModelNotFoundException("Hero no encontrado con ID: " + id));
    }

    private void apply(IsadecorHeroRequest request, IsadecorHero entity, UnidadNegocio unidad) {
        entity.setUnidadNegocio(unidad);
        entity.setTitulo(request.titulo().trim());
        entity.setSubtitulo(request.subtitulo().trim());
        entity.setActivo(request.activo());
    }

    private IsadecorHeroResponse toResponse(IsadecorHero e) {
        return new IsadecorHeroResponse(e.getId(), e.getUnidadNegocio().getId(), e.getTitulo(), e.getSubtitulo(), e.getActivo(), e.getFechaCreacion(), e.getFechaActualizacion());
    }
}
