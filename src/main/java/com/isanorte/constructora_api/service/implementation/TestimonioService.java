package com.isanorte.constructora_api.service.implementation;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.isanorte.constructora_api.dto.request.ActivoRequest;
import com.isanorte.constructora_api.dto.request.TestimonioRequest;
import com.isanorte.constructora_api.dto.response.TestimonioResponse;
import com.isanorte.constructora_api.exception.ModelNotFoundException;
import com.isanorte.constructora_api.model.Testimonio;
import com.isanorte.constructora_api.repository.TestimonioRepository;
import com.isanorte.constructora_api.service.ITestimonioService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TestimonioService implements ITestimonioService {

    private final TestimonioRepository repository;

    @Override
    public List<TestimonioResponse> findAll() {
        return repository.findAllByOrderByOrdenAscIdAsc().stream().map(this::toResponse).toList();
    }

    @Override
    public TestimonioResponse findById(UUID id) {
        return toResponse(findEntity(id));
    }

    @Override
    @Transactional
    public TestimonioResponse create(TestimonioRequest request) {
        Testimonio entity = new Testimonio();
        apply(request, entity);
        return toResponse(repository.save(entity));
    }

    @Override
    @Transactional
    public TestimonioResponse update(UUID id, TestimonioRequest request) {
        Testimonio entity = findEntity(id);
        apply(request, entity);
        return toResponse(repository.saveAndFlush(entity));
    }

    @Override
    @Transactional
    public TestimonioResponse updateActivo(UUID id, ActivoRequest request) {
        Testimonio entity = findEntity(id);
        entity.setActivo(request.activo());
        return toResponse(repository.saveAndFlush(entity));
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        repository.delete(findEntity(id));
    }

    private Testimonio findEntity(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ModelNotFoundException("Testimonio no encontrado con ID: " + id));
    }

    private void apply(TestimonioRequest request, Testimonio entity) {
        entity.setAutor(request.autor().trim());
        entity.setCargo(trimToNull(request.cargo()));
        entity.setTexto(request.texto().trim());
        entity.setProyectoSlug(trimToNull(request.proyectoSlug()));
        entity.setFotoUrl(trimToNull(request.fotoUrl()));
        entity.setFotoAlt(trimToNull(request.fotoAlt()));
        entity.setActivo(request.activo());
        entity.setOrden(request.orden());
    }

    private String trimToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    private TestimonioResponse toResponse(Testimonio entity) {
        return new TestimonioResponse(entity.getId(), entity.getAutor(), entity.getCargo(), entity.getTexto(), entity.getProyectoSlug(), entity.getFotoUrl(), entity.getFotoAlt(), entity.getActivo(), entity.getOrden(),
                entity.getFechaCreacion(), entity.getFechaActualizacion());
    }
}
