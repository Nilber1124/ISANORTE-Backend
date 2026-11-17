package com.isanorte.constructora_api.service.implementation;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.isanorte.constructora_api.dto.request.ActivoRequest;
import com.isanorte.constructora_api.dto.request.PoliticaRequest;
import com.isanorte.constructora_api.dto.response.PoliticaResponse;
import com.isanorte.constructora_api.exception.ModelNotFoundException;
import com.isanorte.constructora_api.model.Politica;
import com.isanorte.constructora_api.repository.PoliticaRepository;
import com.isanorte.constructora_api.service.IPoliticaService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PoliticaService implements IPoliticaService {

    private final PoliticaRepository repository;

    @Override
    public List<PoliticaResponse> findAll() {
        return repository.findAllByOrderByOrdenAscIdAsc().stream().map(this::toResponse).toList();
    }

    @Override
    public PoliticaResponse findById(UUID id) {
        return toResponse(findEntity(id));
    }

    @Override
    @Transactional
    public PoliticaResponse create(PoliticaRequest request) {
        if (repository.existsBySlug(request.slug().trim())) {
            throw new IllegalStateException("Ya existe una política con el slug: " + request.slug().trim());
        }
        Politica entity = new Politica();
        apply(request, entity);
        return toResponse(repository.save(entity));
    }

    @Override
    @Transactional
    public PoliticaResponse update(UUID id, PoliticaRequest request) {
        Politica entity = findEntity(id);
        if (repository.existsBySlugAndIdNot(request.slug().trim(), id)) {
            throw new IllegalStateException("Ya existe una política con el slug: " + request.slug().trim());
        }
        apply(request, entity);
        return toResponse(repository.saveAndFlush(entity));
    }

    @Override
    @Transactional
    public PoliticaResponse updateActivo(UUID id, ActivoRequest request) {
        Politica entity = findEntity(id);
        entity.setActivo(request.activo());
        return toResponse(repository.saveAndFlush(entity));
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        repository.delete(findEntity(id));
    }

    private Politica findEntity(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ModelNotFoundException("Política no encontrada con ID: " + id));
    }

    private void apply(PoliticaRequest request, Politica entity) {
        entity.setTitulo(request.titulo().trim());
        entity.setContenido(request.contenido().trim());
        entity.setSlug(request.slug().trim());
        entity.setActivo(request.activo());
        entity.setOrden(request.orden());
    }

    private PoliticaResponse toResponse(Politica entity) {
        return new PoliticaResponse(entity.getId(), entity.getTitulo(), entity.getContenido(), entity.getSlug(), entity.getActivo(), entity.getOrden(),
                entity.getFechaCreacion(), entity.getFechaActualizacion());
    }
}
