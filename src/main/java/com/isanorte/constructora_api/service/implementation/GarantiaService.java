package com.isanorte.constructora_api.service.implementation;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.isanorte.constructora_api.dto.request.ActivoRequest;
import com.isanorte.constructora_api.dto.request.GarantiaRequest;
import com.isanorte.constructora_api.dto.response.GarantiaResponse;
import com.isanorte.constructora_api.exception.ModelNotFoundException;
import com.isanorte.constructora_api.model.Garantia;
import com.isanorte.constructora_api.repository.GarantiaRepository;
import com.isanorte.constructora_api.service.IGarantiaService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GarantiaService implements IGarantiaService {

    private final GarantiaRepository repository;

    @Override
    public List<GarantiaResponse> findAll() {
        return repository.findAllByOrderByOrdenAscIdAsc().stream().map(this::toResponse).toList();
    }

    @Override
    public GarantiaResponse findById(UUID id) {
        return toResponse(findEntity(id));
    }

    @Override
    @Transactional
    public GarantiaResponse create(GarantiaRequest request) {
        Garantia entity = new Garantia();
        apply(request, entity);
        return toResponse(repository.save(entity));
    }

    @Override
    @Transactional
    public GarantiaResponse update(UUID id, GarantiaRequest request) {
        Garantia entity = findEntity(id);
        apply(request, entity);
        return toResponse(repository.saveAndFlush(entity));
    }

    @Override
    @Transactional
    public GarantiaResponse updateActivo(UUID id, ActivoRequest request) {
        Garantia entity = findEntity(id);
        entity.setActivo(request.activo());
        return toResponse(repository.saveAndFlush(entity));
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        repository.delete(findEntity(id));
    }

    private Garantia findEntity(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ModelNotFoundException("Garantía no encontrado con ID: " + id));
    }

    private void apply(GarantiaRequest request, Garantia entity) {
        entity.setTitulo(request.titulo().trim());
        entity.setTexto(request.texto().trim());
        entity.setEnHero(request.enHero());
        entity.setActivo(request.activo());
        entity.setOrden(request.orden());
    }

    private String trimToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    private GarantiaResponse toResponse(Garantia entity) {
        return new GarantiaResponse(entity.getId(), entity.getTitulo(), entity.getTexto(), entity.getEnHero(), entity.getActivo(), entity.getOrden(),
                entity.getFechaCreacion(), entity.getFechaActualizacion());
    }
}
