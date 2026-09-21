package com.isanorte.constructora_api.service.implementation;

import java.util.List;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.isanorte.constructora_api.dto.request.ProductoRequest;
import com.isanorte.constructora_api.dto.request.EstadoPublicacionRequest;
import com.isanorte.constructora_api.dto.request.ConfiguracionCalculoRequest;
import com.isanorte.constructora_api.dto.request.DocumentoProductoRequest;
import com.isanorte.constructora_api.dto.request.EspecificacionProductoRequest;
import com.isanorte.constructora_api.dto.request.ImagenProductoRequest;
import com.isanorte.constructora_api.dto.request.ProductoUpdateRequest;
import com.isanorte.constructora_api.dto.request.VarianteProductoRequest;
import com.isanorte.constructora_api.dto.response.ConfiguracionCalculoResponse;
import com.isanorte.constructora_api.dto.response.DocumentoProductoResponse;
import com.isanorte.constructora_api.dto.response.EspecificacionProductoResponse;
import com.isanorte.constructora_api.dto.response.ImagenProductoResponse;
import com.isanorte.constructora_api.dto.response.ProductoResponse;
import com.isanorte.constructora_api.dto.response.VarianteProductoResponse;
import com.isanorte.constructora_api.enums.EstadoPublicacion;
import com.isanorte.constructora_api.exception.ModelNotFoundException;
import com.isanorte.constructora_api.mapper.ProductoMapper;
import com.isanorte.constructora_api.model.CategoriaProducto;
import com.isanorte.constructora_api.model.ConfiguracionCalculo;
import com.isanorte.constructora_api.model.DocumentoProducto;
import com.isanorte.constructora_api.model.EspecificacionProducto;
import com.isanorte.constructora_api.model.ImagenProducto;
import com.isanorte.constructora_api.model.Producto;
import com.isanorte.constructora_api.model.UnidadNegocio;
import com.isanorte.constructora_api.model.VarianteProducto;
import com.isanorte.constructora_api.repository.CategoriaProductoRepository;
import com.isanorte.constructora_api.repository.ConfiguracionCalculoRepository;
import com.isanorte.constructora_api.repository.DocumentoProductoRepository;
import com.isanorte.constructora_api.repository.EspecificacionProductoRepository;
import com.isanorte.constructora_api.repository.IGenericRepository;
import com.isanorte.constructora_api.repository.ImagenProductoRepository;
import com.isanorte.constructora_api.repository.ProductoRepository;
import com.isanorte.constructora_api.repository.UnidadNegocioRepository;
import com.isanorte.constructora_api.repository.VarianteProductoRepository;
import com.isanorte.constructora_api.service.IProductoService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductoService extends GenericService<Producto, UUID> implements IProductoService {

    private final ProductoRepository productoRepository;
    private final UnidadNegocioRepository unidadNegocioRepository;
    private final CategoriaProductoRepository categoriaProductoRepository;
    private final VarianteProductoRepository varianteProductoRepository;
    private final ImagenProductoRepository imagenProductoRepository;
    private final EspecificacionProductoRepository especificacionProductoRepository;
    private final DocumentoProductoRepository documentoProductoRepository;
    private final ConfiguracionCalculoRepository configuracionCalculoRepository;
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
        Set<CategoriaProducto> categorias = findCompatibleCategories(request.categoriaIds(), unidad);
        categorias.forEach(producto::addCategoria);
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
        Set<CategoriaProducto> categorias = findCompatibleCategories(request.categoriaIds(), unidad);

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

    @Override
    @Transactional
    public VarianteProductoResponse createVariante(UUID productoId, VarianteProductoRequest request) {
        Producto producto = findProductoParaAdministracion(productoId);
        validarSkuVarianteDisponible(request.sku(), null);
        VarianteProducto variante = productoMapper.toVarianteEntity(request);
        producto.addVariante(variante);
        varianteProductoRepository.saveAndFlush(variante);
        return productoMapper.toVarianteResponse(variante);
    }

    @Override
    @Transactional
    public VarianteProductoResponse updateVariante(
            UUID productoId, UUID varianteId, VarianteProductoRequest request) {
        Producto producto = findProductoParaAdministracion(productoId);
        VarianteProducto variante = findVarianteDelProducto(producto, varianteId);
        validarSkuVarianteDisponible(request.sku(), varianteId);
        productoMapper.updateVarianteEntity(request, variante);
        varianteProductoRepository.saveAndFlush(variante);
        return productoMapper.toVarianteResponse(variante);
    }

    @Override
    @Transactional
    public void deleteVariante(UUID productoId, UUID varianteId) {
        Producto producto = findProductoParaAdministracion(productoId);
        producto.removeVariante(findVarianteDelProducto(producto, varianteId));
        productoRepository.flush();
    }

    @Override
    @Transactional
    public ImagenProductoResponse createImagen(UUID productoId, ImagenProductoRequest request) {
        Producto producto = findProductoParaAdministracion(productoId);
        ImagenProducto imagen = productoMapper.toImagenEntity(request);
        producto.addImagen(imagen);
        imagenProductoRepository.saveAndFlush(imagen);
        return productoMapper.toImagenResponse(imagen);
    }

    @Override
    @Transactional
    public ImagenProductoResponse updateImagen(UUID productoId, UUID imagenId, ImagenProductoRequest request) {
        Producto producto = findProductoParaAdministracion(productoId);
        ImagenProducto imagen = findImagenDelProducto(producto, imagenId);
        productoMapper.updateImagenEntity(request, imagen);
        imagenProductoRepository.saveAndFlush(imagen);
        return productoMapper.toImagenResponse(imagen);
    }

    @Override
    @Transactional
    public void deleteImagen(UUID productoId, UUID imagenId) {
        Producto producto = findProductoParaAdministracion(productoId);
        producto.removeImagen(findImagenDelProducto(producto, imagenId));
        productoRepository.flush();
    }

    @Override
    @Transactional
    public EspecificacionProductoResponse createEspecificacion(
            UUID productoId, EspecificacionProductoRequest request) {
        Producto producto = findProductoParaAdministracion(productoId);
        EspecificacionProducto especificacion = productoMapper.toEspecificacionEntity(request);
        producto.addEspecificacion(especificacion);
        especificacionProductoRepository.saveAndFlush(especificacion);
        return productoMapper.toEspecificacionResponse(especificacion);
    }

    @Override
    @Transactional
    public EspecificacionProductoResponse updateEspecificacion(
            UUID productoId, UUID especificacionId, EspecificacionProductoRequest request) {
        Producto producto = findProductoParaAdministracion(productoId);
        EspecificacionProducto especificacion = findEspecificacionDelProducto(producto, especificacionId);
        productoMapper.updateEspecificacionEntity(request, especificacion);
        especificacionProductoRepository.saveAndFlush(especificacion);
        return productoMapper.toEspecificacionResponse(especificacion);
    }

    @Override
    @Transactional
    public void deleteEspecificacion(UUID productoId, UUID especificacionId) {
        Producto producto = findProductoParaAdministracion(productoId);
        producto.removeEspecificacion(findEspecificacionDelProducto(producto, especificacionId));
        productoRepository.flush();
    }

    @Override
    @Transactional
    public DocumentoProductoResponse createDocumento(UUID productoId, DocumentoProductoRequest request) {
        Producto producto = findProductoParaAdministracion(productoId);
        DocumentoProducto documento = productoMapper.toDocumentoEntity(request);
        producto.addDocumento(documento);
        documentoProductoRepository.saveAndFlush(documento);
        return productoMapper.toDocumentoResponse(documento);
    }

    @Override
    @Transactional
    public DocumentoProductoResponse updateDocumento(
            UUID productoId, UUID documentoId, DocumentoProductoRequest request) {
        Producto producto = findProductoParaAdministracion(productoId);
        DocumentoProducto documento = findDocumentoDelProducto(producto, documentoId);
        productoMapper.updateDocumentoEntity(request, documento);
        documentoProductoRepository.saveAndFlush(documento);
        return productoMapper.toDocumentoResponse(documento);
    }

    @Override
    @Transactional
    public void deleteDocumento(UUID productoId, UUID documentoId) {
        Producto producto = findProductoParaAdministracion(productoId);
        producto.removeDocumento(findDocumentoDelProducto(producto, documentoId));
        productoRepository.flush();
    }

    @Override
    @Transactional
    public ConfiguracionCalculoResponse upsertConfiguracionCalculo(
            UUID productoId, ConfiguracionCalculoRequest request) {
        Producto producto = findProductoParaAdministracion(productoId);
        ConfiguracionCalculo configuracion = producto.getConfiguracionCalculo();
        if (configuracion == null) {
            configuracion = productoMapper.toConfiguracionCalculoEntity(request);
            producto.setConfiguracionCalculo(configuracion);
        } else {
            productoMapper.updateConfiguracionCalculoEntity(request, configuracion);
        }
        configuracionCalculoRepository.saveAndFlush(configuracion);
        return productoMapper.toConfiguracionCalculoResponse(configuracion);
    }

    private Producto findProductoParaAdministracion(UUID productoId) {
        return productoRepository.findById(productoId)
                .orElseThrow(() -> new ModelNotFoundException("Producto no encontrado con ID: " + productoId));
    }

    private Set<CategoriaProducto> findCompatibleCategories(Set<UUID> categoryIds, UnidadNegocio unit) {
        Set<CategoriaProducto> categories = new HashSet<>();
        if (categoryIds == null) {
            return categories;
        }
        for (UUID categoryId : categoryIds) {
            CategoriaProducto category = categoriaProductoRepository.findById(categoryId)
                    .orElseThrow(() -> new ModelNotFoundException(
                            "Categoría de producto no encontrada con ID: " + categoryId));
            validateCategoryCompatibility(category, unit);
            categories.add(category);
        }
        return categories;
    }

    private void validateCategoryCompatibility(CategoriaProducto category, UnidadNegocio unit) {
        UnidadNegocio categoryUnit = category.getUnidadNegocio();
        if (categoryUnit != null && !categoryUnit.getId().equals(unit.getId())) {
            throw new IllegalStateException(
                    "La categoría seleccionada no pertenece a la unidad de negocio del producto.");
        }
    }

    private void validarSkuVarianteDisponible(String sku, UUID varianteId) {
        boolean existe = varianteId == null
                ? varianteProductoRepository.existsBySku(sku)
                : varianteProductoRepository.existsBySkuAndIdNot(sku, varianteId);
        if (existe) {
            throw new IllegalStateException("Ya existe una variante con SKU: " + sku);
        }
    }

    private VarianteProducto findVarianteDelProducto(Producto producto, UUID varianteId) {
        VarianteProducto variante = varianteProductoRepository.findById(varianteId)
                .orElseThrow(() -> new ModelNotFoundException("Variante no encontrada con ID: " + varianteId));
        validarPertenencia(producto, variante.getProducto(), "variante", varianteId);
        return variante;
    }

    private ImagenProducto findImagenDelProducto(Producto producto, UUID imagenId) {
        ImagenProducto imagen = imagenProductoRepository.findById(imagenId)
                .orElseThrow(() -> new ModelNotFoundException("Imagen no encontrada con ID: " + imagenId));
        validarPertenencia(producto, imagen.getProducto(), "imagen", imagenId);
        return imagen;
    }

    private EspecificacionProducto findEspecificacionDelProducto(Producto producto, UUID especificacionId) {
        EspecificacionProducto especificacion = especificacionProductoRepository.findById(especificacionId)
                .orElseThrow(() -> new ModelNotFoundException(
                        "Especificación no encontrada con ID: " + especificacionId));
        validarPertenencia(producto, especificacion.getProducto(), "especificación", especificacionId);
        return especificacion;
    }

    private DocumentoProducto findDocumentoDelProducto(Producto producto, UUID documentoId) {
        DocumentoProducto documento = documentoProductoRepository.findById(documentoId)
                .orElseThrow(() -> new ModelNotFoundException("Documento no encontrado con ID: " + documentoId));
        validarPertenencia(producto, documento.getProducto(), "documento", documentoId);
        return documento;
    }

    private void validarPertenencia(Producto producto, Producto productoDelHijo, String tipo, UUID hijoId) {
        if (productoDelHijo == null || !Objects.equals(producto.getId(), productoDelHijo.getId())) {
            throw new IllegalArgumentException(
                    "El/la " + tipo + " " + hijoId + " no pertenece al producto " + producto.getId());
        }
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
