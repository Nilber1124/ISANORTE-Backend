package com.isanorte.constructora_api.service.implementation;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.isanorte.constructora_api.dto.request.ActivoRequest;
import com.isanorte.constructora_api.dto.request.ServicioRequest;
import com.isanorte.constructora_api.dto.request.ServicioUpdateRequest;
import com.isanorte.constructora_api.dto.response.ServicioResponse;
import com.isanorte.constructora_api.exception.ModelNotFoundException;
import com.isanorte.constructora_api.mapper.DynamicContentMapper;
import com.isanorte.constructora_api.mapper.ServicioMapper;
import com.isanorte.constructora_api.model.BeneficioServicio;
import com.isanorte.constructora_api.repository.BeneficioServicioRepository;
import com.isanorte.constructora_api.repository.ServicioRepository;
import com.isanorte.constructora_api.model.Servicio;

@ExtendWith(MockitoExtension.class)
class ServicioServiceTest {

    @Mock
    private ServicioRepository servicioRepository;
    @Mock
    private ServicioMapper servicioMapper;
    @Mock
    private DynamicContentMapper dynamicContentMapper;
    @Mock
    private BeneficioServicioRepository beneficioServicioRepository;

    @InjectMocks
    private ServicioService servicioService;

    private UUID servicioId;
    private Servicio servicio;

    @BeforeEach
    void setUp() {
        servicioId = UUID.randomUUID();
        servicio = new Servicio();
        servicio.setId(servicioId);
        servicio.setNombre("Construcción");
        servicio.setSlug("construccion");
        servicio.setDescripcion("Descripción");
        servicio.setActivo(true);
        servicio.setDestacado(false);
        servicio.setOrden(0);
    }

    // --- isSlugAvailable ---

    @Test
    void isSlugAvailable_retornaTrue_cuandoSlugNoExiste() {
        when(servicioRepository.existsBySlug("nuevo-slug")).thenReturn(false);

        assertTrue(servicioService.isSlugAvailable("nuevo-slug", null));
    }

    @Test
    void isSlugAvailable_retornaFalse_cuandoSlugExiste() {
        when(servicioRepository.existsBySlug("existente")).thenReturn(true);

        assertFalse(servicioService.isSlugAvailable("existente", null));
    }

    @Test
    void isSlugAvailable_excludeId_retornaTrue_cuandoSlugEsDelMismoServicio() {
        when(servicioRepository.existsBySlugAndIdNot("construccion", servicioId)).thenReturn(false);

        assertTrue(servicioService.isSlugAvailable("construccion", servicioId));
    }

    // --- create: slug duplicado ---

    @Test
    void create_lanzaIllegalState_cuandoSlugYaExiste() {
        ServicioRequest request = new ServicioRequest(
                "Construcción", "construccion", null, "Desc", null, null, null, false, false, null);
        when(servicioRepository.existsBySlug("construccion")).thenReturn(true);

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> servicioService.create(request));
        assertEquals("Ya existe un servicio con esa ruta.", ex.getMessage());
    }

    // --- create: imagen requerida para publicar ---

    @Test
    void create_lanzaIllegalArgument_cuandoActivoSinImagen() {
        ServicioRequest request = new ServicioRequest(
                "Construcción", "construccion", null, "Desc", null, null, null, true, false, null);
        when(servicioRepository.existsBySlug("construccion")).thenReturn(false);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> servicioService.create(request));
        assertEquals("No se puede publicar un servicio sin imagen.", ex.getMessage());
    }

    @Test
    void create_lanzaIllegalArgument_cuandoActivoConImagenBlank() {
        ServicioRequest request = new ServicioRequest(
                "Construcción", "construccion", null, "Desc", "   ", null, null, true, false, null);
        when(servicioRepository.existsBySlug("construccion")).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> servicioService.create(request));
    }

    // --- create: destacado requiere activo ---

    @Test
    void create_lanzaIllegalArgument_cuandoDestacadoSinActivo() {
        ServicioRequest request = new ServicioRequest(
                "Construcción", "construccion", null, "Desc",
                "https://img.com/foto.jpg", null, null, false, true, null);
        when(servicioRepository.existsBySlug("construccion")).thenReturn(false);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> servicioService.create(request));
        assertEquals("No se puede destacar un servicio inactivo.", ex.getMessage());
    }

    // --- create: orden null → max+1 ---

    @Test
    void create_asignaOrdenMaxMasUno_cuandoOrdenEsNull() {
        ServicioRequest request = new ServicioRequest(
                "Construcción", "construccion", null, "Desc",
                "https://img.com/foto.jpg", null, null, true, false, null);
        when(servicioRepository.existsBySlug("construccion")).thenReturn(false);
        when(servicioRepository.findMaxOrden()).thenReturn(5);
        when(servicioMapper.toEntity(request)).thenReturn(servicio);
        when(servicioRepository.save(any(Servicio.class))).thenReturn(servicio);

        servicioService.create(request);

        assertEquals(6, servicio.getOrden());
    }

    @Test
    void create_conservaOrden_cuandoOrdenEsProvisto() {
        ServicioRequest request = new ServicioRequest(
                "Construcción", "construccion", null, "Desc",
                "https://img.com/foto.jpg", null, null, true, false, 2);
        when(servicioRepository.existsBySlug("construccion")).thenReturn(false);
        when(servicioMapper.toEntity(request)).thenReturn(servicio);
        when(servicioRepository.save(any(Servicio.class))).thenReturn(servicio);

        servicioService.create(request);

        verify(servicioRepository, never()).findMaxOrden();
    }

    // --- update: validaciones ---

    @Test
    void update_lanzaIllegalState_cuandoSlugDuplicadoParaOtroServicio() {
        ServicioUpdateRequest request = new ServicioUpdateRequest(
                "Otro", "construccion", null, "Desc", null, null, null, false, false, 0);
        when(servicioRepository.findById(servicioId)).thenReturn(Optional.of(servicio));
        when(servicioRepository.existsBySlugAndIdNot("construccion", servicioId)).thenReturn(true);

        assertThrows(IllegalStateException.class, () -> servicioService.update(servicioId, request));
    }

    @Test
    void update_lanzaIllegalArgument_cuandoActivoSinImagen() {
        ServicioUpdateRequest request = new ServicioUpdateRequest(
                "Construcción", "construccion", null, "Desc", null, null, null, true, false, 0);
        when(servicioRepository.findById(servicioId)).thenReturn(Optional.of(servicio));
        when(servicioRepository.existsBySlugAndIdNot("construccion", servicioId)).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> servicioService.update(servicioId, request));
    }

    // --- updateActivo: imagen requerida ---

    @Test
    void updateActivo_lanzaIllegalArgument_cuandoActivarSinImagen() {
        servicio.setImagenUrl(null);
        ActivoRequest request = new ActivoRequest(true);
        when(servicioRepository.findById(servicioId)).thenReturn(Optional.of(servicio));

        assertThrows(IllegalArgumentException.class,
                () -> servicioService.updateActivo(servicioId, request));
    }

    @Test
    void updateActivo_desactivaDestacado_cuandoSeDesactiva() {
        servicio.setImagenUrl("https://img.com/foto.jpg");
        servicio.setDestacado(true);
        ActivoRequest request = new ActivoRequest(false);
        when(servicioRepository.findById(servicioId)).thenReturn(Optional.of(servicio));
        when(servicioRepository.saveAndFlush(servicio)).thenReturn(servicio);
        when(servicioMapper.toResponse(servicio)).thenReturn(null);

        servicioService.updateActivo(servicioId, request);

        assertFalse(servicio.getDestacado());
    }

    // --- findBySlug: not found ---

    @Test
    void findBySlug_lanzaNotFound_cuandoSlugNoExiste() {
        when(servicioRepository.findBySlug("no-existe")).thenReturn(Optional.empty());

        assertThrows(ModelNotFoundException.class,
                () -> servicioService.findBySlug("no-existe"));
    }
}
