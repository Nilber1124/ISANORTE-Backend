package com.isanorte.constructora_api.service.implementation;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.isanorte.constructora_api.exception.ModelNotFoundException;
import com.isanorte.constructora_api.model.Administrador;
import com.isanorte.constructora_api.repository.AdministradorRepository;
import com.isanorte.constructora_api.repository.IGenericRepository;
import com.isanorte.constructora_api.service.IAdministradorService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdministradorService extends GenericService<Administrador, UUID> implements IAdministradorService {

    private final AdministradorRepository administradorRepository;

    @Override
    protected IGenericRepository<Administrador, UUID> getRepo() {
        return administradorRepository;
    }

    @Override
    public Administrador findByEmail(String email) {
        Administrador administrador = administradorRepository.findByEmail(email)
                .orElseThrow(() -> new ModelNotFoundException("Administrador no encontrado con email: " + email));
        initializeForResponse(administrador);
        return administrador;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Administrador> findAll() {
        List<Administrador> administradores = super.findAll();
        administradores.forEach(this::initializeForResponse);
        return administradores;
    }

    @Override
    @Transactional(readOnly = true)
    public Administrador findById(UUID id) {
        Administrador administrador = super.findById(id);
        initializeForResponse(administrador);
        return administrador;
    }

    private void initializeForResponse(Administrador administrador) {
        administrador.getRoles().size();
    }
}
