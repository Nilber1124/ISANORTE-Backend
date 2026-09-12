package com.isanorte.constructora_api.service.implementation;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.isanorte.constructora_api.model.Empresa;
import com.isanorte.constructora_api.repository.EmpresaRepository;
import com.isanorte.constructora_api.repository.IGenericRepository;
import com.isanorte.constructora_api.service.IEmpresaService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmpresaService extends GenericService<Empresa, UUID> implements IEmpresaService {

    private final EmpresaRepository empresaRepository;

    @Override
    protected IGenericRepository<Empresa, UUID> getRepo() {
        return empresaRepository;
    }
}
