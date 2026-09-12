package com.isanorte.constructora_api.service.implementation;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.isanorte.constructora_api.exception.ModelNotFoundException;
import com.isanorte.constructora_api.model.CategoriaProducto;
import com.isanorte.constructora_api.repository.CategoriaProductoRepository;
import com.isanorte.constructora_api.repository.IGenericRepository;
import com.isanorte.constructora_api.service.ICategoriaProductoService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoriaProductoService extends GenericService<CategoriaProducto, UUID> implements ICategoriaProductoService {

    private final CategoriaProductoRepository categoriaProductoRepository;

    @Override
    protected IGenericRepository<CategoriaProducto, UUID> getRepo() {
        return categoriaProductoRepository;
    }

    @Override
    public CategoriaProducto findBySlug(String slug) {
        return categoriaProductoRepository.findBySlug(slug)
                .orElseThrow(() -> new ModelNotFoundException("Categoría de producto no encontrada con slug: " + slug));
    }

    @Override
    public List<CategoriaProducto> findByActivoTrueOrderByOrdenAsc() {
        return categoriaProductoRepository.findByActivoTrueOrderByOrdenAsc();
    }
}
