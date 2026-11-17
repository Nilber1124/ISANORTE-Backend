package com.isanorte.constructora_api.service.implementation;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.isanorte.constructora_api.dto.request.ActivoRequest;
import com.isanorte.constructora_api.dto.request.ClienteMarcaRequest;
import com.isanorte.constructora_api.dto.response.ClienteMarcaResponse;
import com.isanorte.constructora_api.exception.ModelNotFoundException;
import com.isanorte.constructora_api.model.ClienteMarca;
import com.isanorte.constructora_api.repository.ClienteMarcaRepository;
import com.isanorte.constructora_api.service.IClienteMarcaService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ClienteMarcaService implements IClienteMarcaService {

    private final ClienteMarcaRepository repository;

    @Override
    public List<ClienteMarcaResponse> findAll() {
        return repository.findAllByOrderByOrdenAscIdAsc().stream().map(this::toResponse).toList();
    }

    @Override
    public ClienteMarcaResponse findById(UUID id) {
        return toResponse(findEntity(id));
    }

    @Override
    @Transactional
    public ClienteMarcaResponse create(ClienteMarcaRequest request) {
        ClienteMarca entity = new ClienteMarca();
        apply(request, entity);
        return toResponse(repository.save(entity));
    }

    @Override
    @Transactional
    public ClienteMarcaResponse update(UUID id, ClienteMarcaRequest request) {
        ClienteMarca entity = findEntity(id);
        apply(request, entity);
        return toResponse(repository.saveAndFlush(entity));
    }

    @Override
    @Transactional
    public ClienteMarcaResponse updateActivo(UUID id, ActivoRequest request) {
        ClienteMarca entity = findEntity(id);
        entity.setActivo(request.activo());
        return toResponse(repository.saveAndFlush(entity));
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        repository.delete(findEntity(id));
    }

    private ClienteMarca findEntity(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ModelNotFoundException("Cliente o marca no encontrado con ID: " + id));
    }

    private void apply(ClienteMarcaRequest request, ClienteMarca entity) {
        entity.setNombre(request.nombre().trim());
        entity.setLogoUrl(trimToNull(request.logoUrl()));
        entity.setEnlace(trimToNull(request.enlace()));
        entity.setActivo(request.activo());
        entity.setOrden(request.orden());
    }

    private String trimToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    private ClienteMarcaResponse toResponse(ClienteMarca entity) {
        return new ClienteMarcaResponse(entity.getId(), entity.getNombre(), entity.getLogoUrl(), entity.getEnlace(), entity.getActivo(), entity.getOrden(),
                entity.getFechaCreacion(), entity.getFechaActualizacion());
    }
}
