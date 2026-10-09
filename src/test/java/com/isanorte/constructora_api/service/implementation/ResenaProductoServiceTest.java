package com.isanorte.constructora_api.service.implementation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import com.isanorte.constructora_api.dto.request.CrearResenaRequest;
import com.isanorte.constructora_api.enums.EstadoPublicacion;
import com.isanorte.constructora_api.model.Cliente;
import com.isanorte.constructora_api.model.ConfiguracionSitio;
import com.isanorte.constructora_api.model.Empresa;
import com.isanorte.constructora_api.model.Producto;
import com.isanorte.constructora_api.model.ResenaProducto;
import com.isanorte.constructora_api.model.UnidadNegocio;
import com.isanorte.constructora_api.repository.ClienteRepository;
import com.isanorte.constructora_api.repository.ConfiguracionSitioRepository;
import com.isanorte.constructora_api.repository.ProductoRepository;
import com.isanorte.constructora_api.repository.ResenaProductoRepository;
import com.isanorte.constructora_api.repository.UnidadNegocioRepository;

@ExtendWith(MockitoExtension.class)
class ResenaProductoServiceTest {
    @Mock ConfiguracionSitioRepository configuracionRepository;
    @Mock UnidadNegocioRepository unidadRepository;
    @Mock ProductoRepository productoRepository;
    @Mock ResenaProductoRepository resenaRepository;
    @Mock ClienteRepository clienteRepository;
    private ResenaProductoService service;
    private UUID clienteId;

    @BeforeEach
    void setUp() {
        service = new ResenaProductoService(configuracionRepository, unidadRepository, productoRepository,
                resenaRepository, clienteRepository);
        UUID empresaId = UUID.randomUUID();
        UUID unidadId = UUID.randomUUID();
        Empresa empresa = Empresa.builder().id(empresaId).build();
        ConfiguracionSitio sitio = ConfiguracionSitio.builder().empresa(empresa).build();
        UnidadNegocio unidad = UnidadNegocio.builder().id(unidadId).build();
        Producto producto = Producto.builder().id(UUID.randomUUID()).build();
        when(configuracionRepository.findByClave("isanorte")).thenReturn(Optional.of(sitio));
        when(unidadRepository.findByEmpresaIdAndSlugAndActivoTrue(empresaId, "isadecor"))
                .thenReturn(Optional.of(unidad));
        when(productoRepository.findByUnidadNegocioIdAndSlugAndEstado(unidadId, "panel", EstadoPublicacion.PUBLICADO))
                .thenReturn(Optional.of(producto));
        clienteId = UUID.randomUUID();
    }

    @Test
    void creaResenaConNombreObtenidoDeLaCuentaAutenticada() {
        when(clienteRepository.findById(clienteId)).thenReturn(Optional.of(Cliente.builder()
                .id(clienteId).nombre("Ana").apellido("Pérez").activo(true).build()));
        when(resenaRepository.saveAndFlush(any(ResenaProducto.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var response = service.createPublicReview("isanorte", "isadecor", "panel", clienteId,
                new CrearResenaRequest(5, null, " Excelente "));

        assertEquals("Ana Pérez", response.nombreCliente());
        assertEquals("Excelente", response.comentario());
        ArgumentCaptor<ResenaProducto> captor = ArgumentCaptor.forClass(ResenaProducto.class);
        verify(resenaRepository).saveAndFlush(captor.capture());
        assertEquals(clienteId, captor.getValue().getClienteId());
    }

    @Test
    void rechazaCuentaInactivaSinGuardarResena() {
        when(clienteRepository.findById(clienteId)).thenReturn(Optional.of(Cliente.builder()
                .id(clienteId).nombre("Ana").activo(false).build()));

        assertThrows(AccessDeniedException.class, () -> service.createPublicReview(
                "isanorte", "isadecor", "panel", clienteId, new CrearResenaRequest(4, null, "Comentario")));
        verify(resenaRepository, never()).saveAndFlush(any());
    }
}
