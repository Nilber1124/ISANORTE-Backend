package com.isanorte.constructora_api.service.implementation;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.isanorte.constructora_api.dto.request.EmpresaRequest;
import com.isanorte.constructora_api.mapper.EmpresaMapper;
import com.isanorte.constructora_api.model.Empresa;
import com.isanorte.constructora_api.repository.EmpresaRepository;
import com.isanorte.constructora_api.repository.IGenericRepository;
import com.isanorte.constructora_api.service.IEmpresaService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmpresaService extends GenericService<Empresa, UUID> implements IEmpresaService {

    private final EmpresaRepository empresaRepository;
    private final EmpresaMapper empresaMapper;

    @Override
    protected IGenericRepository<Empresa, UUID> getRepo() {
        return empresaRepository;
    }

    @Override
    @Transactional
    public Empresa create(EmpresaRequest request) {
        Empresa empresa = empresaMapper.toEntity(request);
        if (request.redesSociales() != null) {
            request.redesSociales().stream()
                    .map(empresaMapper::toRedSocialEntity)
                    .forEach(empresa::addRedSocial);
        }
        return empresaRepository.save(empresa);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Empresa> findAll() {
        List<Empresa> empresas = super.findAll();
        empresas.forEach(this::initializeForResponse);
        return empresas;
    }

    @Override
    @Transactional(readOnly = true)
    public Empresa findById(UUID id) {
        Empresa empresa = super.findById(id);
        initializeForResponse(empresa);
        return empresa;
    }

    private void initializeForResponse(Empresa empresa) {
        empresa.getRedesSociales().size();
    }
}
