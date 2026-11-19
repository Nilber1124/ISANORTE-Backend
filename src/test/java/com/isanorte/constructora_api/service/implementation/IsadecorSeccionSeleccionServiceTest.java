package com.isanorte.constructora_api.service.implementation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.isanorte.constructora_api.dto.request.SeccionProductosIdsRequest;
import com.isanorte.constructora_api.exception.ModelNotFoundException;
import com.isanorte.constructora_api.model.IsadecorSeccionExcluido;
import com.isanorte.constructora_api.model.IsadecorSeccionFijado;
import com.isanorte.constructora_api.model.IsadecorSeccionProductos;
import com.isanorte.constructora_api.model.Producto;
import com.isanorte.constructora_api.repository.IsadecorSeccionExcluidoRepository;
import com.isanorte.constructora_api.repository.IsadecorSeccionFijadoRepository;
import com.isanorte.constructora_api.repository.IsadecorSeccionProductosRepository;
import com.isanorte.constructora_api.repository.ProductoRepository;

@ExtendWith(MockitoExtension.class)
class IsadecorSeccionSeleccionServiceTest {

    @Mock private IsadecorSeccionProductosRepository seccionRepository;
    @Mock private IsadecorSeccionFijadoRepository fijadoRepository;
    @Mock private IsadecorSeccionExcluidoRepository excluidoRepository;
    @Mock private ProductoRepository productoRepository;
    @InjectMocks private IsadecorSeccionSeleccionService service;

    private final UUID seccionId = UUID.randomUUID();

    private IsadecorSeccionProductos seccion() {
        IsadecorSeccionProductos seccion = new IsadecorSeccionProductos();
        seccion.setId(seccionId);
        return seccion;
    }

    private Producto producto(String nombre) {
        return Producto.builder().id(UUID.randomUUID()).nombre(nombre).slug(nombre.toLowerCase()).build();
    }

    @Test
    void reemplazarFijadosBorraYGuardaConElOrdenDeLaLista() {
        Producto a = producto("A");
        Producto b = producto("B");
        when(seccionRepository.findById(seccionId)).thenReturn(Optional.of(seccion()));
        when(productoRepository.findAllById(List.of(b.getId(), a.getId()))).thenReturn(List.of(a, b));
        when(fijadoRepository.findBySeccionIdOrderByOrdenAscIdAsc(seccionId)).thenReturn(List.of(
                IsadecorSeccionFijado.builder().producto(b).orden(0).build(),
                IsadecorSeccionFijado.builder().producto(a).orden(1).build()));

        var resultado = service.reemplazarFijados(seccionId, new SeccionProductosIdsRequest(List.of(b.getId(), a.getId())));

        InOrder orden = inOrder(fijadoRepository);
        orden.verify(fijadoRepository).deleteBySeccionId(seccionId);
        orden.verify(fijadoRepository).flush();
        verify(fijadoRepository, times(2)).save(any(IsadecorSeccionFijado.class));
        assertEquals(2, resultado.size());
        assertEquals("B", resultado.get(0).nombre());
    }

    @Test
    void reemplazarFijadosConListaVaciaLimpiaLaSeleccion() {
        when(seccionRepository.findById(seccionId)).thenReturn(Optional.of(seccion()));
        when(productoRepository.findAllById(List.of())).thenReturn(List.of());
        when(fijadoRepository.findBySeccionIdOrderByOrdenAscIdAsc(seccionId)).thenReturn(List.of());

        var resultado = service.reemplazarFijados(seccionId, new SeccionProductosIdsRequest(List.of()));

        assertTrue(resultado.isEmpty());
        verify(fijadoRepository).deleteBySeccionId(seccionId);
        verify(fijadoRepository, never()).save(any(IsadecorSeccionFijado.class));
    }

    @Test
    void reemplazarFijadosRechazaProductosRepetidos() {
        UUID id = UUID.randomUUID();
        when(seccionRepository.findById(seccionId)).thenReturn(Optional.of(seccion()));

        assertThrows(IllegalArgumentException.class,
                () -> service.reemplazarFijados(seccionId, new SeccionProductosIdsRequest(List.of(id, id))));
        verify(fijadoRepository, never()).deleteBySeccionId(any());
    }

    @Test
    void reemplazarFijadosRechazaProductosInexistentes() {
        UUID desconocido = UUID.randomUUID();
        when(seccionRepository.findById(seccionId)).thenReturn(Optional.of(seccion()));
        when(productoRepository.findAllById(List.of(desconocido))).thenReturn(List.of());

        assertThrows(ModelNotFoundException.class,
                () -> service.reemplazarFijados(seccionId, new SeccionProductosIdsRequest(List.of(desconocido))));
        verify(fijadoRepository, never()).deleteBySeccionId(any());
    }

    @Test
    void fijadosLanzaNoEncontradoSiLaSeccionNoExiste() {
        when(seccionRepository.findById(seccionId)).thenReturn(Optional.empty());

        assertThrows(ModelNotFoundException.class, () -> service.fijados(seccionId));
    }

    @Test
    void fijadosDevuelveLosProductosEnOrden() {
        Producto a = producto("A");
        when(seccionRepository.findById(seccionId)).thenReturn(Optional.of(seccion()));
        when(fijadoRepository.findBySeccionIdOrderByOrdenAscIdAsc(seccionId))
                .thenReturn(List.of(IsadecorSeccionFijado.builder().producto(a).orden(0).build()));

        var resultado = service.fijados(seccionId);

        assertEquals(1, resultado.size());
        assertEquals(0, resultado.get(0).orden());
    }

    @Test
    void reemplazarExcluidosGuardaLosProductosIndicados() {
        Producto a = producto("A");
        when(seccionRepository.findById(seccionId)).thenReturn(Optional.of(seccion()));
        when(productoRepository.findAllById(List.of(a.getId()))).thenReturn(List.of(a));
        when(excluidoRepository.findBySeccionId(seccionId))
                .thenReturn(List.of(IsadecorSeccionExcluido.builder().producto(a).build()));

        var resultado = service.reemplazarExcluidos(seccionId, new SeccionProductosIdsRequest(List.of(a.getId())));

        verify(excluidoRepository).deleteBySeccionId(seccionId);
        verify(excluidoRepository).save(any(IsadecorSeccionExcluido.class));
        assertEquals(1, resultado.size());
        assertEquals(null, resultado.get(0).orden());
    }

    @Test
    void reemplazarExcluidosRechazaRepetidos() {
        UUID id = UUID.randomUUID();
        when(seccionRepository.findById(seccionId)).thenReturn(Optional.of(seccion()));

        assertThrows(IllegalArgumentException.class,
                () -> service.reemplazarExcluidos(seccionId, new SeccionProductosIdsRequest(List.of(id, id))));
    }

    @Test
    void excluidosDevuelveListaVaciaSinRegistros() {
        when(seccionRepository.findById(seccionId)).thenReturn(Optional.of(seccion()));
        when(excluidoRepository.findBySeccionId(seccionId)).thenReturn(List.of());

        assertTrue(service.excluidos(seccionId).isEmpty());
    }
}
