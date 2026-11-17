package com.isanorte.constructora_api.service.implementation;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.isanorte.constructora_api.dto.request.ActivoRequest;
import com.isanorte.constructora_api.dto.request.FaqRequest;
import com.isanorte.constructora_api.dto.response.FaqResponse;
import com.isanorte.constructora_api.exception.ModelNotFoundException;
import com.isanorte.constructora_api.model.Faq;
import com.isanorte.constructora_api.repository.FaqRepository;
import com.isanorte.constructora_api.service.IFaqService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FaqService implements IFaqService {

    private final FaqRepository repository;

    @Override
    public List<FaqResponse> findAll() {
        return repository.findAllByOrderByOrdenAscIdAsc().stream().map(this::toResponse).toList();
    }

    @Override
    public FaqResponse findById(UUID id) {
        return toResponse(findEntity(id));
    }

    @Override
    @Transactional
    public FaqResponse create(FaqRequest request) {
        Faq entity = new Faq();
        apply(request, entity);
        return toResponse(repository.save(entity));
    }

    @Override
    @Transactional
    public FaqResponse update(UUID id, FaqRequest request) {
        Faq entity = findEntity(id);
        apply(request, entity);
        return toResponse(repository.saveAndFlush(entity));
    }

    @Override
    @Transactional
    public FaqResponse updateActivo(UUID id, ActivoRequest request) {
        Faq entity = findEntity(id);
        entity.setActivo(request.activo());
        return toResponse(repository.saveAndFlush(entity));
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        repository.delete(findEntity(id));
    }

    private Faq findEntity(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ModelNotFoundException("Pregunta frecuente no encontrado con ID: " + id));
    }

    private void apply(FaqRequest request, Faq entity) {
        entity.setPregunta(request.pregunta().trim());
        entity.setRespuesta(request.respuesta().trim());
        entity.setAmbito(request.ambito().trim());
        entity.setActivo(request.activo());
        entity.setOrden(request.orden());
    }

    private String trimToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    private FaqResponse toResponse(Faq entity) {
        return new FaqResponse(entity.getId(), entity.getPregunta(), entity.getRespuesta(), entity.getAmbito(), entity.getActivo(), entity.getOrden(),
                entity.getFechaCreacion(), entity.getFechaActualizacion());
    }
}
