package com.isanorte.constructora_api.service.implementation;

import com.isanorte.constructora_api.dto.request.ActivoRequest;
import com.isanorte.constructora_api.dto.request.IsadecorCampanaRequest;
import com.isanorte.constructora_api.dto.response.IsadecorCampanaResponse;
import com.isanorte.constructora_api.exception.ModelNotFoundException;
import com.isanorte.constructora_api.model.IsadecorCampana;
import com.isanorte.constructora_api.model.UnidadNegocio;
import com.isanorte.constructora_api.repository.IsadecorCampanaRepository;
import com.isanorte.constructora_api.repository.UnidadNegocioRepository;
import com.isanorte.constructora_api.service.IIsadecorCampanaService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class IsadecorCampanaService implements IIsadecorCampanaService {

    private final IsadecorCampanaRepository repository;
    private final UnidadNegocioRepository unidadRepository;

    @Override
    public List<IsadecorCampanaResponse> findAll() {
        return repository.findAllByOrderByFechaCreacionAscIdAsc().stream().map(this::toResponse).toList();
    }

    @Override
    public IsadecorCampanaResponse findById(UUID id) {
        return toResponse(findEntity(id));
    }

    @Override
    @Transactional
    public IsadecorCampanaResponse create(IsadecorCampanaRequest request) {
        UnidadNegocio unidad = unidadRepository.findById(request.unidadNegocioId())
                .orElseThrow(() -> new ModelNotFoundException("Unidad de negocio no encontrada con ID: " + request.unidadNegocioId()));

        IsadecorCampana entity = new IsadecorCampana();
        apply(request, entity, unidad);
        IsadecorCampana saved = repository.save(entity);
        return toResponse(saved);
    }

    @Override
    @Transactional
    public IsadecorCampanaResponse update(UUID id, IsadecorCampanaRequest request) {
        IsadecorCampana entity = findEntity(id);
        UnidadNegocio unidad = unidadRepository.findById(request.unidadNegocioId())
                .orElseThrow(() -> new ModelNotFoundException("Unidad de negocio no encontrada con ID: " + request.unidadNegocioId()));

        apply(request, entity, unidad);
        return toResponse(repository.saveAndFlush(entity));
    }

    @Override
    @Transactional
    public IsadecorCampanaResponse updateActivo(UUID id, ActivoRequest request) {
        IsadecorCampana entity = findEntity(id);
        entity.setActivo(request.activo());
        return toResponse(repository.saveAndFlush(entity));
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        repository.delete(findEntity(id));
    }

    private IsadecorCampana findEntity(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ModelNotFoundException("Campaña no encontrado con ID: " + id));
    }

    private void apply(IsadecorCampanaRequest request, IsadecorCampana entity, UnidadNegocio unidad) {
        entity.setUnidadNegocio(unidad);
        entity.setEtiqueta(request.etiqueta().trim());
        entity.setTitulo(request.titulo().trim());
        entity.setDescripcion(request.descripcion().trim());
        entity.setTextoBoton(request.textoBoton().trim());
        entity.setEnlace(request.enlace().trim());
        entity.setImagenUrl(request.imagenUrl().trim());
        entity.setActivo(request.activo());
    }

    private IsadecorCampanaResponse toResponse(IsadecorCampana e) {
        return new IsadecorCampanaResponse(e.getId(), e.getUnidadNegocio().getId(), e.getEtiqueta(), e.getTitulo(), e.getDescripcion(), e.getTextoBoton(), e.getEnlace(), e.getImagenUrl(), e.getActivo(), e.getFechaCreacion(), e.getFechaActualizacion());
    }
}
