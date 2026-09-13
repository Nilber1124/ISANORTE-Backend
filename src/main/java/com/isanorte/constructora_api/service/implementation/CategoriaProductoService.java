package com.isanorte.constructora_api.service.implementation;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.isanorte.constructora_api.dto.request.CategoriaProductoRequest;
import com.isanorte.constructora_api.exception.ModelNotFoundException;
import com.isanorte.constructora_api.mapper.CategoriaProductoMapper;
import com.isanorte.constructora_api.model.CategoriaProducto;
import com.isanorte.constructora_api.model.UnidadNegocio;
import com.isanorte.constructora_api.repository.CategoriaProductoRepository;
import com.isanorte.constructora_api.repository.IGenericRepository;
import com.isanorte.constructora_api.repository.UnidadNegocioRepository;
import com.isanorte.constructora_api.service.ICategoriaProductoService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoriaProductoService extends GenericService<CategoriaProducto, UUID> implements ICategoriaProductoService {

    private final CategoriaProductoRepository categoriaProductoRepository;
    private final UnidadNegocioRepository unidadNegocioRepository;
    private final CategoriaProductoMapper categoriaProductoMapper;

    @Override
    protected IGenericRepository<CategoriaProducto, UUID> getRepo() {
        return categoriaProductoRepository;
    }

    @Override
    public CategoriaProducto findBySlug(String slug) {
        CategoriaProducto categoria = categoriaProductoRepository.findBySlug(slug)
                .orElseThrow(() -> new ModelNotFoundException("Categoría de producto no encontrada con slug: " + slug));
        initializeForResponse(categoria);
        return categoria;
    }

    @Override
    public List<CategoriaProducto> findByActivoTrueOrderByOrdenAsc() {
        List<CategoriaProducto> categorias = categoriaProductoRepository.findByActivoTrueOrderByOrdenAsc();
        categorias.forEach(this::initializeForResponse);
        return categorias;
    }

    @Override
    @Transactional
    public CategoriaProducto create(CategoriaProductoRequest request) {
        if (categoriaProductoRepository.existsBySlug(request.slug())) {
            throw new IllegalStateException("Ya existe una categoría con slug: " + request.slug());
        }
        CategoriaProducto categoria = categoriaProductoMapper.toEntity(request);
        if (request.unidadNegocioId() != null) {
            UnidadNegocio unidad = unidadNegocioRepository.findById(request.unidadNegocioId())
                    .orElseThrow(() -> new ModelNotFoundException(
                            "Unidad de negocio no encontrada con ID: " + request.unidadNegocioId()));
            categoria.setUnidadNegocio(unidad);
        }
        return categoriaProductoRepository.save(categoria);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoriaProducto> findAll() {
        List<CategoriaProducto> categorias = super.findAll();
        categorias.forEach(this::initializeForResponse);
        return categorias;
    }

    @Override
    @Transactional(readOnly = true)
    public CategoriaProducto findById(UUID id) {
        CategoriaProducto categoria = super.findById(id);
        initializeForResponse(categoria);
        return categoria;
    }

    private void initializeForResponse(CategoriaProducto categoria) {
        if (categoria.getUnidadNegocio() != null) {
            categoria.getUnidadNegocio().getNombre();
        }
    }
}
