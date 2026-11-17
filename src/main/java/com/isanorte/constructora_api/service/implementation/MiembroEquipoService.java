package com.isanorte.constructora_api.service.implementation;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.isanorte.constructora_api.dto.request.ActivoRequest;
import com.isanorte.constructora_api.dto.request.MiembroEquipoRequest;
import com.isanorte.constructora_api.dto.response.MiembroEquipoResponse;
import com.isanorte.constructora_api.exception.ModelNotFoundException;
import com.isanorte.constructora_api.model.MiembroEquipo;
import com.isanorte.constructora_api.repository.MiembroEquipoRepository;
import com.isanorte.constructora_api.service.IMiembroEquipoService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MiembroEquipoService implements IMiembroEquipoService {

    private final MiembroEquipoRepository repository;

    @Override
    public List<MiembroEquipoResponse> findAll() {
        return repository.findAllByOrderByOrdenAscIdAsc().stream().map(this::toResponse).toList();
    }

    @Override
    public MiembroEquipoResponse findById(UUID id) {
        return toResponse(findEntity(id));
    }

    @Override
    @Transactional
    public MiembroEquipoResponse create(MiembroEquipoRequest request) {
        MiembroEquipo entity = new MiembroEquipo();
        apply(request, entity);
        return toResponse(repository.save(entity));
    }

    @Override
    @Transactional
    public MiembroEquipoResponse update(UUID id, MiembroEquipoRequest request) {
        MiembroEquipo entity = findEntity(id);
        apply(request, entity);
        return toResponse(repository.saveAndFlush(entity));
    }

    @Override
    @Transactional
    public MiembroEquipoResponse updateActivo(UUID id, ActivoRequest request) {
        MiembroEquipo entity = findEntity(id);
        entity.setActivo(request.activo());
        return toResponse(repository.saveAndFlush(entity));
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        repository.delete(findEntity(id));
    }

    private MiembroEquipo findEntity(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ModelNotFoundException("Miembro del equipo no encontrado con ID: " + id));
    }

    private void apply(MiembroEquipoRequest request, MiembroEquipo entity) {
        entity.setNombre(request.nombre().trim());
        entity.setCargo(request.cargo().trim());
        entity.setEspecialidad(trimToNull(request.especialidad()));
        entity.setBiografia(trimToNull(request.biografia()));
        entity.setFotoUrl(trimToNull(request.fotoUrl()));
        entity.setFotoAlt(trimToNull(request.fotoAlt()));
        entity.setActivo(request.activo());
        entity.setOrden(request.orden());
    }

    private String trimToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    private MiembroEquipoResponse toResponse(MiembroEquipo entity) {
        return new MiembroEquipoResponse(entity.getId(), entity.getNombre(), entity.getCargo(), entity.getEspecialidad(), entity.getBiografia(), entity.getFotoUrl(), entity.getFotoAlt(), entity.getActivo(), entity.getOrden(),
                entity.getFechaCreacion(), entity.getFechaActualizacion());
    }
}
