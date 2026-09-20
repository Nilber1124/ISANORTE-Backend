package com.isanorte.constructora_api.service.implementation;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.isanorte.constructora_api.dto.request.EmpresaRequest;
import com.isanorte.constructora_api.dto.request.EmpresaUpdateRequest;
import com.isanorte.constructora_api.dto.request.RedSocialRequest;
import com.isanorte.constructora_api.dto.response.EmpresaResponse;
import com.isanorte.constructora_api.dto.response.RedSocialResponse;
import com.isanorte.constructora_api.mapper.EmpresaMapper;
import com.isanorte.constructora_api.mapper.RedSocialMapper;
import com.isanorte.constructora_api.model.Empresa;
import com.isanorte.constructora_api.model.RedSocial;
import com.isanorte.constructora_api.repository.EmpresaRepository;
import com.isanorte.constructora_api.repository.IGenericRepository;
import com.isanorte.constructora_api.repository.RedSocialRepository;
import com.isanorte.constructora_api.service.IEmpresaService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmpresaService extends GenericService<Empresa, UUID> implements IEmpresaService {

    private final EmpresaRepository empresaRepository;
    private final RedSocialRepository redSocialRepository;
    private final EmpresaMapper empresaMapper;
    private final RedSocialMapper redSocialMapper;
    private final com.isanorte.constructora_api.repository.EstadisticaEmpresaRepository estadisticaEmpresaRepository;
    private final com.isanorte.constructora_api.mapper.DynamicContentMapper dynamicContentMapper;

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
                    .map(redSocialMapper::toEntity)
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

    @Override
    @Transactional(readOnly = true)
    public List<EmpresaResponse> findAllResponse() {
        return super.findAll().stream().map(empresaMapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public EmpresaResponse findByIdResponse(UUID id) {
        return empresaMapper.toResponse(super.findById(id));
    }

    @Override
    @Transactional
    public EmpresaResponse createResponse(EmpresaRequest request) {
        return empresaMapper.toResponse(create(request));
    }

    @Override
    @Transactional
    public EmpresaResponse update(UUID id, EmpresaUpdateRequest request) {
        Empresa empresa = super.findById(id);
        if (empresaRepository.existsByRucAndIdNot(request.ruc(), id)) {
            throw new IllegalStateException("Ya existe una empresa con RUC: " + request.ruc());
        }
        empresaMapper.updateEntity(request, empresa);
        return empresaMapper.toResponse(empresaRepository.saveAndFlush(empresa));
    }

    @Override
    @Transactional
    public RedSocialResponse createRedSocial(UUID empresaId, RedSocialRequest request) {
        Empresa empresa = findEmpresaParaAdministracion(empresaId);
        RedSocial redSocial = redSocialMapper.toEntity(request);
        empresa.addRedSocial(redSocial);
        return redSocialMapper.toResponse(redSocialRepository.saveAndFlush(redSocial));
    }

    @Override
    @Transactional
    public RedSocialResponse updateRedSocial(UUID empresaId, UUID redSocialId, RedSocialRequest request) {
        Empresa empresa = findEmpresaParaAdministracion(empresaId);
        RedSocial redSocial = findRedSocialDeEmpresa(empresa, redSocialId);
        redSocialMapper.updateEntity(request, redSocial);
        return redSocialMapper.toResponse(redSocialRepository.saveAndFlush(redSocial));
    }

    @Override
    @Transactional
    public void deleteRedSocial(UUID empresaId, UUID redSocialId) {
        Empresa empresa = findEmpresaParaAdministracion(empresaId);
        empresa.removeRedSocial(findRedSocialDeEmpresa(empresa, redSocialId));
        empresaRepository.flush();
    }

    @Override
    @Transactional
    public com.isanorte.constructora_api.dto.response.EstadisticaEmpresaResponse createEstadistica(
            UUID empresaId, com.isanorte.constructora_api.dto.request.EstadisticaEmpresaRequest request) {
        Empresa empresa = findEmpresaParaAdministracion(empresaId);
        com.isanorte.constructora_api.model.EstadisticaEmpresa estadistica = dynamicContentMapper.toEntity(request);
        empresa.addEstadistica(estadistica);
        return dynamicContentMapper.toResponse(estadisticaEmpresaRepository.saveAndFlush(estadistica));
    }

    @Override
    @Transactional
    public com.isanorte.constructora_api.dto.response.EstadisticaEmpresaResponse updateEstadistica(
            UUID empresaId, UUID estadisticaId,
            com.isanorte.constructora_api.dto.request.EstadisticaEmpresaRequest request) {
        Empresa empresa = findEmpresaParaAdministracion(empresaId);
        com.isanorte.constructora_api.model.EstadisticaEmpresa estadistica = findEstadistica(empresa, estadisticaId);
        dynamicContentMapper.update(request, estadistica);
        return dynamicContentMapper.toResponse(estadisticaEmpresaRepository.saveAndFlush(estadistica));
    }

    @Override
    @Transactional
    public void deleteEstadistica(UUID empresaId, UUID estadisticaId) {
        Empresa empresa = findEmpresaParaAdministracion(empresaId);
        empresa.removeEstadistica(findEstadistica(empresa, estadisticaId));
        empresaRepository.flush();
    }

    private com.isanorte.constructora_api.model.EstadisticaEmpresa findEstadistica(Empresa empresa, UUID id) {
        com.isanorte.constructora_api.model.EstadisticaEmpresa estadistica = estadisticaEmpresaRepository.findById(id)
                .orElseThrow(() -> new com.isanorte.constructora_api.exception.ModelNotFoundException(
                        "Estadística no encontrada con ID: " + id));
        if (estadistica.getEmpresa() == null || !Objects.equals(empresa.getId(), estadistica.getEmpresa().getId())) {
            throw new IllegalArgumentException("La estadística no pertenece a la empresa indicada");
        }
        return estadistica;
    }

    private Empresa findEmpresaParaAdministracion(UUID empresaId) {
        return empresaRepository.findById(empresaId)
                .orElseThrow(() -> new com.isanorte.constructora_api.exception.ModelNotFoundException(
                        "Empresa no encontrada con ID: " + empresaId));
    }

    private RedSocial findRedSocialDeEmpresa(Empresa empresa, UUID redSocialId) {
        RedSocial redSocial = redSocialRepository.findById(redSocialId)
                .orElseThrow(() -> new com.isanorte.constructora_api.exception.ModelNotFoundException(
                        "Red social no encontrada con ID: " + redSocialId));
        if (redSocial.getEmpresa() == null || !Objects.equals(empresa.getId(), redSocial.getEmpresa().getId())) {
            throw new IllegalArgumentException(
                    "La red social " + redSocialId + " no pertenece a la empresa " + empresa.getId());
        }
        return redSocial;
    }

    private void initializeForResponse(Empresa empresa) {
        empresa.getRedesSociales().size();
        empresa.getEstadisticas().size();
    }
}
