package com.isanorte.constructora_api.service.implementation;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.isanorte.constructora_api.dto.request.EmpresaRequest;
import com.isanorte.constructora_api.dto.request.EmpresaUpdateRequest;
import com.isanorte.constructora_api.dto.request.RedSocialRequest;
import com.isanorte.constructora_api.dto.response.EmpresaResponse;
import com.isanorte.constructora_api.dto.response.RedSocialResponse;
import com.isanorte.constructora_api.mapper.DynamicContentMapper;
import com.isanorte.constructora_api.mapper.EmpresaMapper;
import com.isanorte.constructora_api.mapper.RedSocialMapper;
import com.isanorte.constructora_api.model.Empresa;
import com.isanorte.constructora_api.model.RedSocial;
import com.isanorte.constructora_api.repository.EmpresaRepository;
import com.isanorte.constructora_api.repository.EstadisticaEmpresaRepository;
import com.isanorte.constructora_api.repository.RedSocialRepository;
import com.isanorte.constructora_api.exception.ModelNotFoundException;

@ExtendWith(MockitoExtension.class)
class EmpresaServiceTest {

    @Mock
    private EmpresaRepository empresaRepository;
    @Mock
    private RedSocialRepository redSocialRepository;
    @Mock
    private EmpresaMapper empresaMapper;
    @Mock
    private RedSocialMapper redSocialMapper;
    @Mock
    private EstadisticaEmpresaRepository estadisticaEmpresaRepository;
    @Mock
    private DynamicContentMapper dynamicContentMapper;

    @InjectMocks
    private EmpresaService empresaService;

    private Empresa empresa;
    private UUID empresaId;

    @BeforeEach
    void setUp() {
        empresaId = UUID.randomUUID();
        empresa = new Empresa();
        empresa.setId(empresaId);
        empresa.setRuc("20123456789");
    }

    @Test
    void create_debeRetornarEmpresa_cuandoDatosSonValidos() {
        EmpresaRequest request = new EmpresaRequest("ISANORTE", "Isanorte SAC", "20123456789", "Dir", "Ciudad", "tel1", null, "email@test.com", null, null, null, null, null, null, null, List.of());
        when(empresaMapper.toEntity(request)).thenReturn(empresa);
        when(empresaRepository.save(empresa)).thenReturn(empresa);

        Empresa result = empresaService.create(request);

        assertNotNull(result);
        assertEquals(empresaId, result.getId());
        verify(empresaRepository, times(1)).save(empresa);
    }

    @Test
    void update_debeActualizarEmpresa_cuandoRucNoEsDuplicado() {
        EmpresaUpdateRequest request = new EmpresaUpdateRequest("ISANORTE", "Isanorte SAC", "20123456789", "Dir", "Ciudad", "tel1", null, "email@test.com", null, null, null, null, null, null, null);
        when(empresaRepository.findById(empresaId)).thenReturn(Optional.of(empresa));
        when(empresaRepository.existsByRucAndIdNot(request.ruc(), empresaId)).thenReturn(false);
        when(empresaRepository.saveAndFlush(empresa)).thenReturn(empresa);
        EmpresaResponse response = new EmpresaResponse(empresaId, "ISANORTE", "Isanorte SAC", "20123456789", "Dir", "Ciudad", "tel1", null, "email@test.com", null, null, null, null, null, null, null, List.of(), List.of(), null, null);
        when(empresaMapper.toResponse(empresa)).thenReturn(response);

        EmpresaResponse result = empresaService.update(empresaId, request);

        assertNotNull(result);
        assertEquals("ISANORTE", result.razonSocial());
        verify(empresaMapper, times(1)).updateEntity(request, empresa);
        verify(empresaRepository, times(1)).saveAndFlush(empresa);
    }

    @Test
    void update_debeLanzarExcepcion_cuandoRucEstaDuplicado() {
        EmpresaUpdateRequest request = new EmpresaUpdateRequest("ISANORTE", "Isanorte SAC", "20123456789", "Dir", "Ciudad", "tel1", null, "email@test.com", null, null, null, null, null, null, null);
        when(empresaRepository.findById(empresaId)).thenReturn(Optional.of(empresa));
        when(empresaRepository.existsByRucAndIdNot(request.ruc(), empresaId)).thenReturn(true);

        assertThrows(IllegalStateException.class, () -> empresaService.update(empresaId, request));
        verify(empresaMapper, never()).updateEntity(any(), any());
        verify(empresaRepository, never()).saveAndFlush(any());
    }

    @Test
    void createRedSocial_debeCrearRedSocialCorrectamente() {
        RedSocialRequest request = new RedSocialRequest("Facebook", "url", "icono", 1, true);
        RedSocial redSocial = new RedSocial();
        when(empresaRepository.findById(empresaId)).thenReturn(Optional.of(empresa));
        when(redSocialMapper.toEntity(request)).thenReturn(redSocial);
        when(redSocialRepository.saveAndFlush(redSocial)).thenReturn(redSocial);
        RedSocialResponse response = new RedSocialResponse(UUID.randomUUID(), "Facebook", "url", "icono", 1, true);
        when(redSocialMapper.toResponse(redSocial)).thenReturn(response);

        RedSocialResponse result = empresaService.createRedSocial(empresaId, request);

        assertNotNull(result);
        assertEquals("Facebook", result.nombre());
        assertTrue(empresa.getRedesSociales().contains(redSocial));
        verify(redSocialRepository, times(1)).saveAndFlush(redSocial);
    }

    @Test
    void deleteRedSocial_debeEliminarRedSocial_cuandoPerteneceAEmpresa() {
        UUID redSocialId = UUID.randomUUID();
        RedSocial redSocial = new RedSocial();
        redSocial.setId(redSocialId);
        redSocial.setEmpresa(empresa);
        empresa.addRedSocial(redSocial);

        when(empresaRepository.findById(empresaId)).thenReturn(Optional.of(empresa));
        when(redSocialRepository.findById(redSocialId)).thenReturn(Optional.of(redSocial));

        empresaService.deleteRedSocial(empresaId, redSocialId);

        assertFalse(empresa.getRedesSociales().contains(redSocial));
        verify(empresaRepository, times(1)).flush();
    }

    @Test
    void deleteRedSocial_debeLanzarExcepcion_cuandoRedSocialNoPerteneceAEmpresa() {
        UUID redSocialId = UUID.randomUUID();
        RedSocial redSocial = new RedSocial();
        redSocial.setId(redSocialId);
        Empresa otraEmpresa = new Empresa();
        otraEmpresa.setId(UUID.randomUUID());
        redSocial.setEmpresa(otraEmpresa);

        when(empresaRepository.findById(empresaId)).thenReturn(Optional.of(empresa));
        when(redSocialRepository.findById(redSocialId)).thenReturn(Optional.of(redSocial));

        assertThrows(IllegalArgumentException.class, () -> empresaService.deleteRedSocial(empresaId, redSocialId));
    }
}
