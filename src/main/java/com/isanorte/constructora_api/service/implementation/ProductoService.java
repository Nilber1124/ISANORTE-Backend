package com.isanorte.constructora_api.service.implementation;

import java.util.List;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.isanorte.constructora_api.dto.request.ProductoRequest;
import com.isanorte.constructora_api.dto.request.EstadoPublicacionRequest;
import com.isanorte.constructora_api.dto.request.ProductoUpdateRequest;
import com.isanorte.constructora_api.dto.response.ProductoResponse;
import com.isanorte.constructora_api.enums.EstadoPublicacion;
import com.isanorte.constructora_api.exception.ModelNotFoundException;
import com.isanorte.constructora_api.mapper.ProductoMapper;
import com.isanorte.constructora_api.model.CategoriaProducto;
import com.isanorte.constructora_api.model.Producto;
import com.isanorte.constructora_api.model.UnidadNegocio;
import com.isanorte.constructora_api.repository.CategoriaProductoRepository;
import com.isanorte.constructora_api.repository.IGenericRepository;
import com.isanorte.constructora_api.repository.ProductoRepository;
import com.isanorte.constructora_api.repository.UnidadNegocioRepository;
import com.isanorte.constructora_api.service.IProductoService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductoService extends GenericService<Producto, UUID> implements IProductoService {

    private final ProductoRepository productoRepository;
    private final UnidadNegocioRepository unidadNegocioRepository;
    private final CategoriaProductoRepository categoriaProductoRepository;
    private final ProductoMapper productoMapper;

    @Override
    protected IGenericRepository<Producto, UUID> getRepo() {
        return productoRepository;
    }

    @Override
    public Producto findBySlug(String slug) {
        Producto producto = productoRepository.findBySlug(slug)
                .orElseThrow(() -> new ModelNotFoundException("Producto no encontrado con slug: " + slug));
        initializeForResponse(producto);
        return producto;
    }

    @Override
    public Producto findBySku(String sku) {
        Producto producto = productoRepository.findBySku(sku)
                .orElseThrow(() -> new ModelNotFoundException("Producto no encontrado con SKU: " + sku));
        initializeForResponse(producto);
        return producto;
    }

    @Override
    @Transactional
    public Producto create(ProductoRequest request) {
        if (productoRepository.existsBySku(request.sku())) {
            throw new IllegalStateException("Ya existe un producto con SKU: " + request.sku());
        }
        if (productoRepository.existsBySlug(request.slug())) {
            throw new IllegalStateException("Ya existe un producto con slug: " + request.slug());
        }
        UnidadNegocio unidad = unidadNegocioRepository.findById(request.unidadNegocioId())
                .orElseThrow(() -> new ModelNotFoundException(
                        "Unidad de negocio no encontrada con ID: " + request.unidadNegocioId()));
        Producto producto = productoMapper.toEntity(request);
        unidad.addProducto(producto);
        if (request.variantes() != null) {
            request.variantes().stream()
                    .map(productoMapper::toVarianteEntity)
                    .forEach(producto::addVariante);
        }
        if (request.imagenes() != null) {
            request.imagenes().stream()
                    .map(productoMapper::toImagenEntity)
                    .forEach(producto::addImagen);
        }
        if (request.especificaciones() != null) {
            request.especificaciones().stream()
                    .map(productoMapper::toEspecificacionEntity)
                    .forEach(producto::addEspecificacion);
        }
        if (request.documentos() != null) {
            request.documentos().stream()
                    .map(productoMapper::toDocumentoEntity)
                    .forEach(producto::addDocumento);
        }
        if (request.configuracionCalculo() != null) {
            producto.setConfiguracionCalculo(
                    productoMapper.toConfiguracionCalculoEntity(request.configuracionCalculo()));
        }
        if (request.categoriaIds() != null) {
            for (UUID categoriaId : request.categoriaIds()) {
                CategoriaProducto categoria = categoriaProductoRepository.findById(categoriaId)
                        .orElseThrow(() -> new ModelNotFoundException(
                                "Categoría de producto no encontrada con ID: " + categoriaId));
                producto.addCategoria(categoria);
            }
        }
        return productoRepository.save(producto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Producto> findAll() {
        List<Producto> productos = super.findAll();
        productos.forEach(this::initializeForResponse);
        return productos;
    }

    @Override
    @Transactional(readOnly = true)
    public Producto findById(UUID id) {
        Producto producto = super.findById(id);
        initializeForResponse(producto);
        return producto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductoResponse> findAllResponse() {
        return super.findAll().stream().map(productoMapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ProductoResponse findByIdResponse(UUID id) {
        return productoMapper.toResponse(super.findById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public ProductoResponse findBySlugResponse(String slug) {
        Producto producto = productoRepository.findBySlug(slug)
                .orElseThrow(() -> new ModelNotFoundException("Producto no encontrado con slug: " + slug));
        return productoMapper.toResponse(producto);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductoResponse findBySkuResponse(String sku) {
        Producto producto = productoRepository.findBySku(sku)
                .orElseThrow(() -> new ModelNotFoundException("Producto no encontrado con SKU: " + sku));
        return productoMapper.toResponse(producto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductoResponse> findPublishedResponses() {
        return productoRepository.findByEstado(EstadoPublicacion.PUBLICADO).stream()
                .map(productoMapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ProductoResponse findPublishedBySlugResponse(String slug) {
        Producto producto = productoRepository.findBySlugAndEstado(slug, EstadoPublicacion.PUBLICADO)
                .orElseThrow(() -> new ModelNotFoundException("Producto publicado no encontrado con slug: " + slug));
        return productoMapper.toResponse(producto);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductoResponse findPublishedBySkuResponse(String sku) {
        Producto producto = productoRepository.findBySkuAndEstado(sku, EstadoPublicacion.PUBLICADO)
                .orElseThrow(() -> new ModelNotFoundException("Producto publicado no encontrado con SKU: " + sku));
        return productoMapper.toResponse(producto);
    }

    @Override
    @Transactional
    public ProductoResponse createResponse(ProductoRequest request) {
        return productoMapper.toResponse(create(request));
    }

    @Override
    @Transactional
    public ProductoResponse update(UUID id, ProductoUpdateRequest request) {
        Producto producto = super.findById(id);
        if (productoRepository.existsBySkuAndIdNot(request.sku(), id)) {
            throw new IllegalStateException("Ya existe un producto con SKU: " + request.sku());
        }
        if (productoRepository.existsBySlugAndIdNot(request.slug(), id)) {
            throw new IllegalStateException("Ya existe un producto con slug: " + request.slug());
        }

        UnidadNegocio unidad = unidadNegocioRepository.findById(request.unidadNegocioId())
                .orElseThrow(() -> new ModelNotFoundException(
                        "Unidad de negocio no encontrada con ID: " + request.unidadNegocioId()));
        Set<CategoriaProducto> categorias = new HashSet<>();
        for (UUID categoriaId : request.categoriaIds()) {
            categorias.add(categoriaProductoRepository.findById(categoriaId)
                    .orElseThrow(() -> new ModelNotFoundException(
                            "Categoría de producto no encontrada con ID: " + categoriaId)));
        }

        productoMapper.updateEntity(request, producto);
        if (!producto.getUnidadNegocio().getId().equals(unidad.getId())) {
            producto.cambiarUnidadNegocio(unidad);
        }
        for (CategoriaProducto actual : new HashSet<>(producto.getCategorias())) {
            if (!request.categoriaIds().contains(actual.getId())) {
                producto.removeCategoria(actual);
            }
        }
        Set<UUID> idsActuales = producto.getCategorias().stream()
                .map(CategoriaProducto::getId)
                .collect(java.util.stream.Collectors.toSet());
        for (CategoriaProducto categoria : categorias) {
            if (!idsActuales.contains(categoria.getId())) {
                producto.addCategoria(categoria);
            }
        }
        return productoMapper.toResponse(productoRepository.saveAndFlush(producto));
    }

    @Override
    @Transactional
    public ProductoResponse updateEstado(UUID id, EstadoPublicacionRequest request) {
        Producto producto = super.findById(id);
        producto.setEstado(request.estado());
        return productoMapper.toResponse(productoRepository.saveAndFlush(producto));
    }

    private void initializeForResponse(Producto producto) {
        producto.getUnidadNegocio().getNombre();
        producto.getCategorias().size();
        producto.getVariantes().size();
        producto.getImagenes().size();
        producto.getEspecificaciones().size();
        producto.getDocumentos().size();
        if (producto.getConfiguracionCalculo() != null) {
            producto.getConfiguracionCalculo().getCoberturaPorUnidad();
        }
    }
}
