package com.isanorte.constructora_api.service.implementation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.isanorte.constructora_api.dto.request.DireccionClienteRequest;
import com.isanorte.constructora_api.dto.response.DireccionClienteResponse;
import com.isanorte.constructora_api.exception.ModelNotFoundException;
import com.isanorte.constructora_api.model.Cliente;
import com.isanorte.constructora_api.model.DireccionCliente;
import com.isanorte.constructora_api.repository.ClienteRepository;
import com.isanorte.constructora_api.repository.DireccionClienteRepository;

class DireccionClienteServiceTest {

    @Test
    void createDebeAsociarLaDireccionAlClienteAutenticado() {
        DireccionClienteRepository direccionRepository = mock(DireccionClienteRepository.class);
        ClienteRepository clienteRepository = mock(ClienteRepository.class);
        UUID clienteId = UUID.randomUUID();
        Cliente cliente = Cliente.builder().id(clienteId).build();
        when(clienteRepository.findById(clienteId)).thenReturn(Optional.of(cliente));
        when(direccionRepository.save(org.mockito.ArgumentMatchers.any(DireccionCliente.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        DireccionClienteService service = new DireccionClienteService(direccionRepository, clienteRepository);

        DireccionClienteResponse response = service.create(clienteId, new DireccionClienteRequest(
                "Casa", "Av. Siempre Viva 123", "Miraflores", "Lima", "Portón azul", "987654321", true));

        assertEquals("Casa", response.alias());
        assertEquals("Av. Siempre Viva 123", response.calle());
        assertEquals(Boolean.TRUE, response.principal());
    }

    @Test
    void updateDebeRechazarSiLaDireccionNoPerteneceAlCliente() {
        DireccionClienteRepository direccionRepository = mock(DireccionClienteRepository.class);
        ClienteRepository clienteRepository = mock(ClienteRepository.class);
        UUID direccionId = UUID.randomUUID();
        Cliente otroCliente = Cliente.builder().id(UUID.randomUUID()).build();
        DireccionCliente direccion = DireccionCliente.builder().id(direccionId).cliente(otroCliente).build();
        when(direccionRepository.findById(direccionId)).thenReturn(Optional.of(direccion));
        DireccionClienteService service = new DireccionClienteService(direccionRepository, clienteRepository);

        assertThrows(IllegalArgumentException.class, () -> service.update(UUID.randomUUID(), direccionId,
                new DireccionClienteRequest(null, "Calle", "Distrito", "Ciudad", null, "999999999", false)));
    }

    @Test
    void deleteDebeFallarSiLaDireccionNoExiste() {
        DireccionClienteRepository direccionRepository = mock(DireccionClienteRepository.class);
        ClienteRepository clienteRepository = mock(ClienteRepository.class);
        UUID direccionId = UUID.randomUUID();
        when(direccionRepository.findById(direccionId)).thenReturn(Optional.empty());
        DireccionClienteService service = new DireccionClienteService(direccionRepository, clienteRepository);

        assertThrows(ModelNotFoundException.class, () -> service.delete(UUID.randomUUID(), direccionId));
        verify(direccionRepository, org.mockito.Mockito.never()).delete(org.mockito.ArgumentMatchers.any());
    }
}
