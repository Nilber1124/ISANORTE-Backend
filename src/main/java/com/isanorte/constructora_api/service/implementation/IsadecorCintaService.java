package com.isanorte.constructora_api.service.implementation;

import com.isanorte.constructora_api.dto.request.ActivoRequest;
import com.isanorte.constructora_api.dto.request.IsadecorCintaRequest;
import com.isanorte.constructora_api.dto.response.IsadecorCintaResponse;
import com.isanorte.constructora_api.exception.ModelNotFoundException;
import com.isanorte.constructora_api.model.IsadecorCinta;
import com.isanorte.constructora_api.model.UnidadNegocio;
import com.isanorte.constructora_api.repository.IsadecorCintaRepository;
import com.isanorte.constructora_api.repository.UnidadNegocioRepository;
import com.isanorte.constructora_api.service.IIsadecorCintaService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class IsadecorCintaService implements IIsadecorCintaService {

    private final IsadecorCintaRepository repository;
    private final UnidadNegocioRepository unidadRepository;

    @Override
    public List<IsadecorCintaResponse> findAll() {
        return repository.findAllByOrderByOrdenAscIdAsc().stream().map(this::toResponse).toList();
    }

    @Override
    public IsadecorCintaResponse findById(UUID id) {
        return toResponse(findEntity(id));
    }

    @Override
    @Transactional
    public IsadecorCintaResponse create(IsadecorCintaRequest request) {
        UnidadNegocio unidad = unidadRepository.findById(request.unidadNegocioId())
                .orElseThrow(() -> new ModelNotFoundException("Unidad de negocio no encontrada con ID: " + request.unidadNegocioId()));

        IsadecorCinta entity = new IsadecorCinta();
        apply(request, entity, unidad);
        IsadecorCinta saved = repository.save(entity);
        return toResponse(saved);
    }

    @Override
    @Transactional
    public IsadecorCintaResponse update(UUID id, IsadecorCintaRequest request) {
        IsadecorCinta entity = findEntity(id);
        UnidadNegocio unidad = unidadRepository.findById(request.unidadNegocioId())
                .orElseThrow(() -> new ModelNotFoundException("Unidad de negocio no encontrada con ID: " + request.unidadNegocioId()));

        apply(request, entity, unidad);
        return toResponse(repository.saveAndFlush(entity));
    }

    @Override
    @Transactional
    public IsadecorCintaResponse updateActivo(UUID id, ActivoRequest request) {
        IsadecorCinta entity = findEntity(id);
        entity.setActivo(request.activo());
        return toResponse(repository.saveAndFlush(entity));
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        repository.delete(findEntity(id));
    }

    private IsadecorCinta findEntity(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ModelNotFoundException("Aviso de la cinta no encontrado con ID: " + id));
    }

    private void apply(IsadecorCintaRequest request, IsadecorCinta entity, UnidadNegocio unidad) {
        entity.setUnidadNegocio(unidad);
        entity.setTexto(request.texto().trim());
        entity.setEnlace(trimToNull(request.enlace()));
        entity.setOrden(request.orden());
        entity.setActivo(request.activo());
    }

    private String trimToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    private IsadecorCintaResponse toResponse(IsadecorCinta e) {
        return new IsadecorCintaResponse(e.getId(), e.getUnidadNegocio().getId(), e.getTexto(), e.getEnlace(), e.getOrden(), e.getActivo(), e.getFechaCreacion(), e.getFechaActualizacion());
    }
}
