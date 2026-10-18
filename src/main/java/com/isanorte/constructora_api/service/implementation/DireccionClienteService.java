package com.isanorte.constructora_api.service.implementation;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.isanorte.constructora_api.dto.request.DireccionClienteRequest;
import com.isanorte.constructora_api.dto.response.DireccionClienteResponse;
import com.isanorte.constructora_api.exception.ModelNotFoundException;
import com.isanorte.constructora_api.model.Cliente;
import com.isanorte.constructora_api.model.DireccionCliente;
import com.isanorte.constructora_api.repository.ClienteRepository;
import com.isanorte.constructora_api.repository.DireccionClienteRepository;
import com.isanorte.constructora_api.service.IDireccionClienteService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DireccionClienteService implements IDireccionClienteService {

    private final DireccionClienteRepository direccionRepository;
    private final ClienteRepository clienteRepository;

    @Override
    @Transactional(readOnly = true)
    public List<DireccionClienteResponse> findByCliente(UUID clienteId) {
        return direccionRepository.findByClienteIdOrderByPrincipalDescFechaCreacionDesc(clienteId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public DireccionClienteResponse create(UUID clienteId, DireccionClienteRequest request) {
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new ModelNotFoundException("Cuenta de cliente no encontrada"));
        DireccionCliente direccion = DireccionCliente.builder()
                .cliente(cliente)
                .alias(blankToNull(request.alias()))
                .calle(request.calle().trim())
                .distrito(request.distrito().trim())
                .ciudad(request.ciudad().trim())
                .referencia(blankToNull(request.referencia()))
                .telefonoContacto(request.telefonoContacto().trim())
                .principal(Boolean.TRUE.equals(request.principal()))
                .build();
        return toResponse(direccionRepository.save(direccion));
    }

    @Override
    @Transactional
    public DireccionClienteResponse update(UUID clienteId, UUID direccionId, DireccionClienteRequest request) {
        DireccionCliente direccion = findOwnedDireccion(clienteId, direccionId);
        direccion.setAlias(blankToNull(request.alias()));
        direccion.setCalle(request.calle().trim());
        direccion.setDistrito(request.distrito().trim());
        direccion.setCiudad(request.ciudad().trim());
        direccion.setReferencia(blankToNull(request.referencia()));
        direccion.setTelefonoContacto(request.telefonoContacto().trim());
        direccion.setPrincipal(Boolean.TRUE.equals(request.principal()));
        return toResponse(direccionRepository.saveAndFlush(direccion));
    }

    @Override
    @Transactional
    public void delete(UUID clienteId, UUID direccionId) {
        DireccionCliente direccion = findOwnedDireccion(clienteId, direccionId);
        direccionRepository.delete(direccion);
    }

    private DireccionCliente findOwnedDireccion(UUID clienteId, UUID direccionId) {
        DireccionCliente direccion = direccionRepository.findById(direccionId)
                .orElseThrow(() -> new ModelNotFoundException("Dirección no encontrada con ID: " + direccionId));
        if (!Objects.equals(clienteId, direccion.getCliente().getId())) {
            throw new IllegalArgumentException("La dirección no pertenece a la cuenta indicada");
        }
        return direccion;
    }

    private DireccionClienteResponse toResponse(DireccionCliente direccion) {
        return new DireccionClienteResponse(
                direccion.getId(), direccion.getAlias(), direccion.getCalle(), direccion.getDistrito(),
                direccion.getCiudad(), direccion.getReferencia(), direccion.getTelefonoContacto(),
                direccion.getPrincipal());
    }

    private String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
