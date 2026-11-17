package com.isanorte.constructora_api.service.implementation;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.isanorte.constructora_api.dto.request.ActivoRequest;
import com.isanorte.constructora_api.dto.request.PasoProcesoRequest;
import com.isanorte.constructora_api.dto.response.PasoProcesoResponse;
import com.isanorte.constructora_api.exception.ModelNotFoundException;
import com.isanorte.constructora_api.model.PasoProceso;
import com.isanorte.constructora_api.repository.PasoProcesoRepository;
import com.isanorte.constructora_api.service.IPasoProcesoService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PasoProcesoService implements IPasoProcesoService {

    private final PasoProcesoRepository repository;

    @Override
    public List<PasoProcesoResponse> findAll() {
        return repository.findAllByOrderByOrdenAscIdAsc().stream().map(this::toResponse).toList();
    }

    @Override
    public PasoProcesoResponse findById(UUID id) {
        return toResponse(findEntity(id));
    }

    @Override
    @Transactional
    public PasoProcesoResponse create(PasoProcesoRequest request) {
        PasoProceso entity = new PasoProceso();
        apply(request, entity);
        return toResponse(repository.save(entity));
    }

    @Override
    @Transactional
    public PasoProcesoResponse update(UUID id, PasoProcesoRequest request) {
        PasoProceso entity = findEntity(id);
        apply(request, entity);
        return toResponse(repository.saveAndFlush(entity));
    }

    @Override
    @Transactional
    public PasoProcesoResponse updateActivo(UUID id, ActivoRequest request) {
        PasoProceso entity = findEntity(id);
        entity.setActivo(request.activo());
        return toResponse(repository.saveAndFlush(entity));
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        repository.delete(findEntity(id));
    }

    private PasoProceso findEntity(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ModelNotFoundException("Paso del proceso no encontrado con ID: " + id));
    }

    private void apply(PasoProcesoRequest request, PasoProceso entity) {
        entity.setTitulo(request.titulo().trim());
        entity.setTexto(request.texto().trim());
        entity.setActivo(request.activo());
        entity.setOrden(request.orden());
    }

    private String trimToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    private PasoProcesoResponse toResponse(PasoProceso entity) {
        return new PasoProcesoResponse(entity.getId(), entity.getTitulo(), entity.getTexto(), entity.getActivo(), entity.getOrden(),
                entity.getFechaCreacion(), entity.getFechaActualizacion());
    }
}
