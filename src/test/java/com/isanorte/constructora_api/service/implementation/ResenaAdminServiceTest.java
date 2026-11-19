package com.isanorte.constructora_api.service.implementation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.isanorte.constructora_api.dto.request.ResenaHomeRequest;
import com.isanorte.constructora_api.exception.ModelNotFoundException;
import com.isanorte.constructora_api.model.Producto;
import com.isanorte.constructora_api.model.ResenaProducto;
import com.isanorte.constructora_api.repository.ResenaProductoRepository;

@ExtendWith(MockitoExtension.class)
class ResenaAdminServiceTest {

    @Mock private ResenaProductoRepository repository;
    @InjectMocks private ResenaAdminService service;

    private ResenaProducto resena(String cliente, boolean home, int orden, LocalDateTime fecha) {
        return ResenaProducto.builder().id(UUID.randomUUID()).nombreCliente(cliente).calificacion(5)
                .comentario("ok").mostrarEnHome(home).ordenHome(orden).fechaCreacion(fecha)
                .producto(Producto.builder().id(UUID.randomUUID()).nombre("Panel").build()).build();
    }

    @Test
    void findAllPoneAlPrincipioLasMarcadasPorOrdenYLuegoLasMasRecientes() {
        LocalDateTime hoy = LocalDateTime.now();
        var vieja = resena("Vieja", false, 0, hoy.minusDays(5));
        var nueva = resena("Nueva", false, 0, hoy);
        var segunda = resena("Segunda", true, 2, hoy.minusDays(9));
        var primera = resena("Primera", true, 1, hoy.minusDays(8));
        when(repository.findAll()).thenReturn(List.of(vieja, nueva, segunda, primera));

        var nombres = service.findAll().stream().map(r -> r.nombreCliente()).toList();

        assertEquals(List.of("Primera", "Segunda", "Nueva", "Vieja"), nombres);
    }

    @Test
    void updateHomeGuardaLaMarcaYElOrden() {
        var r = resena("Ana", false, 0, LocalDateTime.now());
        when(repository.findById(r.getId())).thenReturn(Optional.of(r));
        when(repository.save(r)).thenReturn(r);

        var response = service.updateHome(r.getId(), new ResenaHomeRequest(true, 3));

        assertTrue(response.mostrarEnHome());
        assertEquals(3, response.ordenHome());
        assertEquals("Panel", response.productoNombre());
    }

    @Test
    void updateHomeConIdInexistenteLanzaNoEncontrado() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ModelNotFoundException.class, () -> service.updateHome(id, new ResenaHomeRequest(true, 0)));
    }
}
