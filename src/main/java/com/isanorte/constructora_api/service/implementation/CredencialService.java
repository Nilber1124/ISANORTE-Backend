package com.isanorte.constructora_api.service.implementation;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.isanorte.constructora_api.dto.request.ActivoRequest;
import com.isanorte.constructora_api.dto.request.CredencialRequest;
import com.isanorte.constructora_api.dto.response.CredencialResponse;
import com.isanorte.constructora_api.exception.ModelNotFoundException;
import com.isanorte.constructora_api.model.Credencial;
import com.isanorte.constructora_api.repository.CredencialRepository;
import com.isanorte.constructora_api.service.ICredencialService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CredencialService implements ICredencialService {

    private final CredencialRepository repository;

    @Override
    public List<CredencialResponse> findAll() {
        return repository.findAllByOrderByOrdenAscIdAsc().stream().map(this::toResponse).toList();
    }

    @Override
    public CredencialResponse findById(UUID id) {
        return toResponse(findEntity(id));
    }

    @Override
    @Transactional
    public CredencialResponse create(CredencialRequest request) {
        Credencial entity = new Credencial();
        apply(request, entity);
        return toResponse(repository.save(entity));
    }

    @Override
    @Transactional
    public CredencialResponse update(UUID id, CredencialRequest request) {
        Credencial entity = findEntity(id);
        apply(request, entity);
        return toResponse(repository.saveAndFlush(entity));
    }

    @Override
    @Transactional
    public CredencialResponse updateActivo(UUID id, ActivoRequest request) {
        Credencial entity = findEntity(id);
        entity.setActivo(request.activo());
        return toResponse(repository.saveAndFlush(entity));
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        repository.delete(findEntity(id));
    }

    private Credencial findEntity(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ModelNotFoundException("Credencial no encontrado con ID: " + id));
    }

    private void apply(CredencialRequest request, Credencial entity) {
        entity.setNombre(request.nombre().trim());
        entity.setEntidad(trimToNull(request.entidad()));
        entity.setTipo(request.tipo().trim());
        entity.setCodigo(trimToNull(request.codigo()));
        entity.setAnio(request.anio());
        entity.setImagenUrl(trimToNull(request.imagenUrl()));
        entity.setImagenAlt(trimToNull(request.imagenAlt()));
        entity.setActivo(request.activo());
        entity.setOrden(request.orden());
    }

    private String trimToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    private CredencialResponse toResponse(Credencial entity) {
        return new CredencialResponse(entity.getId(), entity.getNombre(), entity.getEntidad(), entity.getTipo(), entity.getCodigo(), entity.getAnio(), entity.getImagenUrl(), entity.getImagenAlt(), entity.getActivo(), entity.getOrden(),
                entity.getFechaCreacion(), entity.getFechaActualizacion());
    }
}
