package com.isanorte.constructora_api.service.implementation;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.isanorte.constructora_api.dto.request.ActivoRequest;
import com.isanorte.constructora_api.dto.request.CategoriaProductoRequest;
import com.isanorte.constructora_api.dto.request.CategoriaProductoUpdateRequest;
import com.isanorte.constructora_api.dto.response.CategoriaProductoResponse;
import com.isanorte.constructora_api.exception.ModelNotFoundException;
import com.isanorte.constructora_api.mapper.CategoriaProductoMapper;
import com.isanorte.constructora_api.model.CategoriaProducto;
import com.isanorte.constructora_api.model.UnidadNegocio;
import com.isanorte.constructora_api.repository.CategoriaProductoRepository;
import com.isanorte.constructora_api.repository.IGenericRepository;
import com.isanorte.constructora_api.repository.ProductoRepository;
import com.isanorte.constructora_api.repository.UnidadNegocioRepository;
import com.isanorte.constructora_api.service.ICategoriaProductoService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoriaProductoService extends GenericService<CategoriaProducto, UUID> implements ICategoriaProductoService {

    private final CategoriaProductoRepository categoriaProductoRepository;
    private final UnidadNegocioRepository unidadNegocioRepository;
    private final ProductoRepository productoRepository;
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
        return createInternal(request);
    }

    private CategoriaProducto createInternal(CategoriaProductoRequest request) {
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
        categoria.setCategoriaPadre(resolveParent(request.categoriaPadreId(), null));
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

    @Override
    @Transactional(readOnly = true)
    public List<CategoriaProductoResponse> findAllResponse() {
        return super.findAll().stream().map(categoriaProductoMapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CategoriaProductoResponse findByIdResponse(UUID id) {
        return categoriaProductoMapper.toResponse(super.findById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public CategoriaProductoResponse findBySlugResponse(String slug) {
        CategoriaProducto categoria = categoriaProductoRepository.findBySlug(slug)
                .orElseThrow(() -> new ModelNotFoundException("Categoría de producto no encontrada con slug: " + slug));
        return categoriaProductoMapper.toResponse(categoria);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoriaProductoResponse> findActiveResponses() {
        return categoriaProductoRepository.findByActivoTrueOrderByOrdenAsc().stream()
                .map(categoriaProductoMapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CategoriaProductoResponse findActiveBySlugResponse(String slug) {
        CategoriaProducto categoria = categoriaProductoRepository.findBySlugAndActivoTrue(slug)
                .orElseThrow(() -> new ModelNotFoundException(
                        "Categoría de producto activa no encontrada con slug: " + slug));
        return categoriaProductoMapper.toResponse(categoria);
    }

    @Override
    @Transactional
    public CategoriaProductoResponse createResponse(CategoriaProductoRequest request) {
        return categoriaProductoMapper.toResponse(createInternal(request));
    }

    @Override
    @Transactional
    public CategoriaProductoResponse update(UUID id, CategoriaProductoUpdateRequest request) {
        CategoriaProducto categoria = super.findById(id);
        if (categoriaProductoRepository.existsBySlugAndIdNot(request.slug(), id)) {
            throw new IllegalStateException("Ya existe una categoría con slug: " + request.slug());
        }
        UnidadNegocio unidad = request.unidadNegocioId() == null
                ? null
                : unidadNegocioRepository.findById(request.unidadNegocioId())
                        .orElseThrow(() -> new ModelNotFoundException(
                                "Unidad de negocio no encontrada con ID: " + request.unidadNegocioId()));
        validateScopeChange(categoria, unidad);
        categoriaProductoMapper.updateEntity(request, categoria);
        categoria.setUnidadNegocio(unidad);
        categoria.setCategoriaPadre(resolveParent(request.categoriaPadreId(), categoria));
        return categoriaProductoMapper.toResponse(categoriaProductoRepository.saveAndFlush(categoria));
    }

    @Override
    @Transactional
    public CategoriaProductoResponse updateActivo(UUID id, ActivoRequest request) {
        CategoriaProducto categoria = super.findById(id);
        categoria.setActivo(request.activo());
        return categoriaProductoMapper.toResponse(categoriaProductoRepository.saveAndFlush(categoria));
    }

    /**
     * Resuelve la categoría padre pedida. Solo hay dos niveles: el padre debe ser una categoría raíz,
     * una categoría no puede ser su propio padre y una categoría con subcategorías no puede colgar de otra.
     */
    private CategoriaProducto resolveParent(UUID parentId, CategoriaProducto current) {
        if (parentId == null) {
            return null;
        }
        if (current != null && parentId.equals(current.getId())) {
            throw new IllegalArgumentException("Una categoría no puede ser su propia categoría padre.");
        }
        CategoriaProducto parent = categoriaProductoRepository.findById(parentId)
                .orElseThrow(() -> new ModelNotFoundException("Categoría padre no encontrada con ID: " + parentId));
        if (parent.getCategoriaPadre() != null) {
            throw new IllegalArgumentException(
                    "Solo se permiten dos niveles: la categoría padre no puede ser a su vez una subcategoría.");
        }
        if (current != null && current.getId() != null
                && categoriaProductoRepository.existsByCategoriaPadre_Id(current.getId())) {
            throw new IllegalArgumentException(
                    "Esta categoría tiene subcategorías, por eso no puede colgar de otra categoría.");
        }
        return parent;
    }

    private void initializeForResponse(CategoriaProducto categoria) {
        if (categoria.getUnidadNegocio() != null) {
            categoria.getUnidadNegocio().getNombre();
        }
    }

    private void validateScopeChange(CategoriaProducto categoria, UnidadNegocio targetUnit) {
        UUID currentUnitId = categoria.getUnidadNegocio() == null ? null : categoria.getUnidadNegocio().getId();
        UUID targetUnitId = targetUnit == null ? null : targetUnit.getId();
        if (java.util.Objects.equals(currentUnitId, targetUnitId) || targetUnitId == null) {
            return;
        }
        if (productoRepository.existsByCategorias_IdAndUnidadNegocio_IdNot(categoria.getId(), targetUnitId)) {
            throw new IllegalStateException(
                    "No se puede cambiar la unidad de la categoría porque dejaría productos incompatibles.");
        }
    }
}
