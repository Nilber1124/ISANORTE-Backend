package com.isanorte.constructora_api.service.implementation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.isanorte.constructora_api.dto.request.CategoriaProductoRequest;
import com.isanorte.constructora_api.dto.request.CategoriaProductoUpdateRequest;
import com.isanorte.constructora_api.exception.ModelNotFoundException;
import com.isanorte.constructora_api.mapper.CategoriaProductoMapper;
import com.isanorte.constructora_api.model.CategoriaProducto;
import com.isanorte.constructora_api.repository.CategoriaProductoRepository;
import com.isanorte.constructora_api.repository.ProductoRepository;
import com.isanorte.constructora_api.repository.UnidadNegocioRepository;

class CategoriaProductoServiceSubcategoriasTest {

    private final CategoriaProductoRepository repo = mock(CategoriaProductoRepository.class);
    private final CategoriaProductoMapper mapper = mock(CategoriaProductoMapper.class);
    private final CategoriaProductoService service = new CategoriaProductoService(
            repo, mock(UnidadNegocioRepository.class), mock(ProductoRepository.class), mapper);

    private static CategoriaProducto categoria(String slug, CategoriaProducto padre) {
        return CategoriaProducto.builder().id(UUID.randomUUID()).nombre(slug).slug(slug).categoriaPadre(padre).build();
    }

    private CategoriaProductoRequest crear(UUID padreId) {
        return new CategoriaProductoRequest("Hija", "hija", null, null, true, 0, null, padreId);
    }

    private CategoriaProductoUpdateRequest actualizar(UUID padreId) {
        return new CategoriaProductoUpdateRequest("Hija", "hija", null, null, true, 0, null, padreId);
    }

    private void preparar(CategoriaProducto nueva) {
        when(mapper.toEntity(any())).thenReturn(nueva);
        when(repo.save(any(CategoriaProducto.class))).thenAnswer(call -> call.getArgument(0));
    }

    @Test
    void crearConPadreRaizLaCuelga() {
        CategoriaProducto padre = categoria("padre", null);
        when(repo.findById(padre.getId())).thenReturn(Optional.of(padre));
        CategoriaProducto nueva = categoria("hija", null);
        preparar(nueva);

        service.create(crear(padre.getId()));

        assertSame(padre, nueva.getCategoriaPadre());
    }

    @Test
    void crearSinPadreLaDejaRaiz() {
        CategoriaProducto nueva = categoria("hija", null);
        preparar(nueva);

        service.create(crear(null));

        assertNull(nueva.getCategoriaPadre());
    }

    @Test
    void rechazaUnPadreQueEsSubcategoria() {
        CategoriaProducto abuelo = categoria("abuelo", null);
        CategoriaProducto padre = categoria("padre", abuelo);
        when(repo.findById(padre.getId())).thenReturn(Optional.of(padre));
        preparar(categoria("hija", null));

        assertThrows(IllegalArgumentException.class, () -> service.create(crear(padre.getId())));
    }

    @Test
    void rechazaUnPadreInexistente() {
        UUID inexistente = UUID.randomUUID();
        when(repo.findById(inexistente)).thenReturn(Optional.empty());
        preparar(categoria("hija", null));

        assertThrows(ModelNotFoundException.class, () -> service.create(crear(inexistente)));
    }

    @Test
    void rechazaSerSuPropioPadre() {
        CategoriaProducto actual = categoria("actual", null);
        when(repo.findById(actual.getId())).thenReturn(Optional.of(actual));

        assertThrows(IllegalArgumentException.class, () -> service.update(actual.getId(), actualizar(actual.getId())));
    }

    @Test
    void rechazaColgarUnaCategoriaQueYaTieneSubcategorias() {
        CategoriaProducto actual = categoria("actual", null);
        CategoriaProducto otraRaiz = categoria("otra", null);
        when(repo.findById(actual.getId())).thenReturn(Optional.of(actual));
        when(repo.findById(otraRaiz.getId())).thenReturn(Optional.of(otraRaiz));
        when(repo.existsByCategoriaPadre_Id(actual.getId())).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> service.update(actual.getId(), actualizar(otraRaiz.getId())));
    }

    @Test
    void actualizarConPadreNuloLaVuelveRaiz() {
        CategoriaProducto padre = categoria("padre", null);
        CategoriaProducto actual = categoria("actual", padre);
        when(repo.findById(actual.getId())).thenReturn(Optional.of(actual));
        when(repo.saveAndFlush(any(CategoriaProducto.class))).thenAnswer(call -> call.getArgument(0));

        service.update(actual.getId(), actualizar(null));

        assertNull(actual.getCategoriaPadre());
        assertEquals("actual", actual.getSlug());
    }
}
