package com.isanorte.constructora_api.service.implementation;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.isanorte.constructora_api.dto.request.EstadoSolicitudContactoRequest;
import com.isanorte.constructora_api.dto.request.SolicitudContactoCreateRequest;
import com.isanorte.constructora_api.dto.response.SolicitudContactoPublicResponse;
import com.isanorte.constructora_api.dto.response.SolicitudContactoResponse;
import com.isanorte.constructora_api.enums.EstadoSolicitudContacto;
import com.isanorte.constructora_api.exception.ModelNotFoundException;
import com.isanorte.constructora_api.mapper.DynamicContentMapper;
import com.isanorte.constructora_api.model.SolicitudContacto;
import com.isanorte.constructora_api.repository.SolicitudContactoRepository;
import com.isanorte.constructora_api.service.ISolicitudContactoService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SolicitudContactoService implements ISolicitudContactoService {
    private final SolicitudContactoRepository repository;
    private final DynamicContentMapper mapper;

    @Override @Transactional
    public SolicitudContactoPublicResponse createPublic(SolicitudContactoCreateRequest request) {
        SolicitudContacto entity = mapper.toEntity(request);
        entity.setEstado(EstadoSolicitudContacto.NUEVA);
        return mapper.toPublicResponse(repository.saveAndFlush(entity));
    }

    @Override @Transactional(readOnly = true)
    public List<SolicitudContactoResponse> findAll(EstadoSolicitudContacto estado) {
        List<SolicitudContacto> values = estado == null
                ? repository.findAllByOrderByFechaCreacionDesc()
                : repository.findByEstadoOrderByFechaCreacionDesc(estado);
        return values.stream().map(mapper::toResponse).toList();
    }

    @Override @Transactional(readOnly = true)
    public SolicitudContactoResponse findById(UUID id) {
        return mapper.toResponse(find(id));
    }

    @Override @Transactional
    public SolicitudContactoResponse updateEstado(UUID id, EstadoSolicitudContactoRequest request) {
        SolicitudContacto entity = find(id);
        entity.setEstado(request.estado());
        return mapper.toResponse(repository.saveAndFlush(entity));
    }

    private SolicitudContacto find(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ModelNotFoundException("Solicitud de contacto no encontrada con ID: " + id));
    }
}
