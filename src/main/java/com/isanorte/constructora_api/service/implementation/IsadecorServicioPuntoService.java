package com.isanorte.constructora_api.service.implementation;

import com.isanorte.constructora_api.dto.request.ActivoRequest;
import com.isanorte.constructora_api.dto.request.IsadecorServicioPuntoRequest;
import com.isanorte.constructora_api.dto.response.IsadecorServicioPuntoResponse;
import com.isanorte.constructora_api.exception.ModelNotFoundException;
import com.isanorte.constructora_api.model.IsadecorServicioEmpresa;
import com.isanorte.constructora_api.model.IsadecorServicioPunto;
import com.isanorte.constructora_api.repository.IsadecorServicioEmpresaRepository;
import com.isanorte.constructora_api.repository.IsadecorServicioPuntoRepository;
import com.isanorte.constructora_api.service.IIsadecorServicioPuntoService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class IsadecorServicioPuntoService implements IIsadecorServicioPuntoService {

    private final IsadecorServicioPuntoRepository repository;
    private final IsadecorServicioEmpresaRepository isadecorServicioEmpresaRepository;

    @Override
    public List<IsadecorServicioPuntoResponse> findAll() {
        return repository.findAllByOrderByOrdenAscIdAsc().stream().map(this::toResponse).toList();
    }

    @Override
    public IsadecorServicioPuntoResponse findById(UUID id) {
        return toResponse(findEntity(id));
    }

    @Override
    @Transactional
    public IsadecorServicioPuntoResponse create(IsadecorServicioPuntoRequest request) {
        IsadecorServicioEmpresa servicio = isadecorServicioEmpresaRepository.findById(request.servicioId())
                .orElseThrow(() -> new ModelNotFoundException("IsadecorServicioEmpresa no encontrado con ID: " + request.servicioId()));

        IsadecorServicioPunto entity = new IsadecorServicioPunto();
        apply(request, entity, servicio);
        IsadecorServicioPunto saved = repository.save(entity);
        return toResponse(saved);
    }

    @Override
    @Transactional
    public IsadecorServicioPuntoResponse update(UUID id, IsadecorServicioPuntoRequest request) {
        IsadecorServicioPunto entity = findEntity(id);
        IsadecorServicioEmpresa servicio = isadecorServicioEmpresaRepository.findById(request.servicioId())
                .orElseThrow(() -> new ModelNotFoundException("IsadecorServicioEmpresa no encontrado con ID: " + request.servicioId()));

        apply(request, entity, servicio);
        return toResponse(repository.saveAndFlush(entity));
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        repository.delete(findEntity(id));
    }

    private IsadecorServicioPunto findEntity(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ModelNotFoundException("Punto del bloque de servicios no encontrado con ID: " + id));
    }

    private void apply(IsadecorServicioPuntoRequest request, IsadecorServicioPunto entity, IsadecorServicioEmpresa servicio) {
        entity.setServicio(servicio);
        entity.setTexto(request.texto().trim());
        entity.setOrden(request.orden());
    }

    private IsadecorServicioPuntoResponse toResponse(IsadecorServicioPunto e) {
        return new IsadecorServicioPuntoResponse(e.getId(), e.getServicio().getId(), e.getTexto(), e.getOrden(), e.getFechaCreacion(), e.getFechaActualizacion());
    }
}
