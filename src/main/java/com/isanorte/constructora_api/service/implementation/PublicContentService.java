package com.isanorte.constructora_api.service.implementation;

import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.isanorte.constructora_api.dto.response.PublicBusinessUnitResponse;
import com.isanorte.constructora_api.dto.response.PublicCompanyAboutResponse;
import com.isanorte.constructora_api.dto.response.PublicCompanyStatisticResponse;
import com.isanorte.constructora_api.dto.response.PublicHomeResponse;
import com.isanorte.constructora_api.dto.response.PublicPageResponse;
import com.isanorte.constructora_api.dto.response.PublicProductCardResponse;
import com.isanorte.constructora_api.dto.response.PublicProductCatalogResponse;
import com.isanorte.constructora_api.dto.response.PublicProductCategoryResponse;
import com.isanorte.constructora_api.dto.response.PublicProductDetailResponse;
import com.isanorte.constructora_api.dto.response.PublicProductDocumentResponse;
import com.isanorte.constructora_api.dto.response.PublicProductImageResponse;
import com.isanorte.constructora_api.dto.response.PublicProductSpecificationResponse;
import com.isanorte.constructora_api.dto.response.PublicProductVariantResponse;
import com.isanorte.constructora_api.dto.response.PublicProjectImageResponse;
import com.isanorte.constructora_api.dto.response.PublicProjectResponse;
import com.isanorte.constructora_api.dto.response.PublicProjectServiceResponse;
import com.isanorte.constructora_api.dto.response.PublicServiceBenefitResponse;
import com.isanorte.constructora_api.dto.response.PublicServiceResponse;
import com.isanorte.constructora_api.dto.response.PublicSiteResponse;
import com.isanorte.constructora_api.enums.EstadoPublicacion;
import com.isanorte.constructora_api.enums.TipoPaginaPublica;
import com.isanorte.constructora_api.enums.TipoPaginaSeo;
import com.isanorte.constructora_api.exception.ModelNotFoundException;
import com.isanorte.constructora_api.model.ConfiguracionSitio;
import com.isanorte.constructora_api.model.ContenidoPagina;
import com.isanorte.constructora_api.model.CategoriaProducto;
import com.isanorte.constructora_api.model.DocumentoProducto;
import com.isanorte.constructora_api.model.EspecificacionProducto;
import com.isanorte.constructora_api.model.ImagenProducto;
import com.isanorte.constructora_api.model.Producto;
import com.isanorte.constructora_api.model.SeoPagina;
import com.isanorte.constructora_api.model.UnidadNegocio;
import com.isanorte.constructora_api.model.VarianteProducto;
import com.isanorte.constructora_api.mapper.PublicProductMapper;
import com.isanorte.constructora_api.repository.ConfiguracionSitioRepository;
import com.isanorte.constructora_api.repository.ContenidoPaginaRepository;
import com.isanorte.constructora_api.repository.EstadisticaEmpresaRepository;
import com.isanorte.constructora_api.repository.ProyectoRepository;
import com.isanorte.constructora_api.repository.ProductoRepository;
import com.isanorte.constructora_api.repository.RedSocialRepository;
import com.isanorte.constructora_api.repository.SeccionLandingRepository;
import com.isanorte.constructora_api.repository.SeoPaginaRepository;
import com.isanorte.constructora_api.repository.ServicioRepository;
import com.isanorte.constructora_api.repository.UnidadNegocioRepository;
import com.isanorte.constructora_api.service.IPublicContentService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PublicContentService implements IPublicContentService {
    private static final Comparator<com.isanorte.constructora_api.model.HeroScene> SCENE_ORDER =
            Comparator.comparing(com.isanorte.constructora_api.model.HeroScene::getOrden)
                    .thenComparing(value -> value.getId().toString());
    private static final Comparator<com.isanorte.constructora_api.model.AccionLanding> ACTION_ORDER =
            Comparator.comparing(com.isanorte.constructora_api.model.AccionLanding::getOrden)
                    .thenComparing(value -> value.getId().toString());
    private static final Comparator<com.isanorte.constructora_api.model.BeneficioServicio> BENEFIT_ORDER =
            Comparator.comparing(com.isanorte.constructora_api.model.BeneficioServicio::getOrden)
                    .thenComparing(value -> value.getId().toString());

    private static final Comparator<com.isanorte.constructora_api.model.RecursoUnidadNegocio> RESOURCE_ORDER =
            Comparator.comparing(com.isanorte.constructora_api.model.RecursoUnidadNegocio::getOrden)
                    .thenComparing(value -> value.getId().toString());
    private static final Comparator<com.isanorte.constructora_api.model.Servicio> PROJECT_SERVICE_ORDER =
            Comparator.comparing(com.isanorte.constructora_api.model.Servicio::getOrden)
                    .thenComparing(value -> value.getId().toString());
    private static final Comparator<ImagenProducto> PRODUCT_IMAGE_ORDER =
            Comparator.comparing((ImagenProducto value) -> Boolean.TRUE.equals(value.getEsPrincipal()) ? 0 : 1)
                    .thenComparing(ImagenProducto::getOrden)
                    .thenComparing(value -> value.getId().toString());
    private static final Comparator<CategoriaProducto> PRODUCT_CATEGORY_ORDER =
            Comparator.comparing(CategoriaProducto::getOrden)
                    .thenComparing(value -> value.getId().toString());
    private static final Comparator<VarianteProducto> PRODUCT_VARIANT_ORDER =
            Comparator.comparing(VarianteProducto::getOrden)
                    .thenComparing(value -> value.getId().toString());
    private static final Comparator<EspecificacionProducto> PRODUCT_SPECIFICATION_ORDER =
            Comparator.comparing(EspecificacionProducto::getOrden)
                    .thenComparing(value -> value.getId().toString());
    private static final Comparator<DocumentoProducto> PRODUCT_DOCUMENT_ORDER =
            Comparator.comparing(DocumentoProducto::getTitulo)
                    .thenComparing(value -> value.getId().toString());

    private final ConfiguracionSitioRepository configuracionRepository;
    private final SeccionLandingRepository seccionRepository;
    private final ServicioRepository servicioRepository;
    private final ProyectoRepository proyectoRepository;
    private final ProductoRepository productoRepository;
    private final UnidadNegocioRepository unidadRepository;
    private final ContenidoPaginaRepository contenidoRepository;
    private final EstadisticaEmpresaRepository estadisticaEmpresaRepository;
    private final SeoPaginaRepository seoRepository;
    private final RedSocialRepository redSocialRepository;
    private final PublicProductMapper publicProductMapper;

    @Override @Transactional(readOnly = true)
    public PublicSiteResponse findSite(String clave) {
        ConfiguracionSitio site = findSiteEntity(clave);
        var company = site.getEmpresa();
        var social = redSocialRepository.findByEmpresaIdAndActivoTrueOrderByOrdenAscIdAsc(company.getId()).stream()
                .map(item -> new PublicSiteResponse.RedPublica(
                        item.getNombre(), item.getUrl(), item.getIcono(), item.getOrden())).toList();
        var units = unidadRepository.findByEmpresaIdAndActivoTrueOrderByOrdenAscIdAsc(company.getId()).stream()
                .map(item -> new PublicSiteResponse.UnidadPublica(
                        item.getNombre(), item.getSlug(), item.getDescripcion(), item.getIcono(),
                        item.getImagenUrl(), item.getImagenAlt(), item.getOrden())).toList();
        var publicCompany = new PublicSiteResponse.EmpresaPublica(
                company.getNombreComercial(), company.getDireccion(), company.getCiudad(), company.getTelefono(),
                company.getTelefonoSecundario(), company.getEmail(), company.getEmailVentas(), company.getWhatsapp(),
                company.getHorarioAtencion(), company.getResumenNosotros());
        return new PublicSiteResponse(site.getClave(), site.getTituloSitio(), site.getDescripcionSitio(),
                site.getLogoUrl(), site.getLogoBlancoUrl(), site.getFaviconUrl(), site.getTextoPiePagina(),
                publicCompany, social, units);
    }

    @Override @Transactional(readOnly = true)
    public PublicHomeResponse findHome(String clave) {
        ConfiguracionSitio site = findSiteEntity(clave);
        var sections = seccionRepository
                .findByConfiguracionSitioIdAndVisibleTrueOrderByOrdenAscIdAsc(site.getId()).stream()
                .map(section -> new PublicHomeResponse.Seccion(
                        section.getTipo(), section.getEtiqueta(), section.getTitulo(), section.getSubtitulo(),
                        section.getContenido(), section.getImagenUrl(), section.getImagenAlt(),
                        section.getTextoBoton(), section.getEnlaceBoton(), section.getOrden(),
                        section.getEscenas().stream().filter(item -> Boolean.TRUE.equals(item.getActivo()))
                                .sorted(SCENE_ORDER)
                                .map(item -> new PublicHomeResponse.Escena(
                                        item.getImagenUrl(), item.getAlt(), item.getOrden())).toList(),
                        section.getAcciones().stream().filter(item -> Boolean.TRUE.equals(item.getActivo()))
                                .sorted(ACTION_ORDER)
                                .map(item -> new PublicHomeResponse.Accion(
                                        item.getTexto(), item.getEnlace(), item.getOrden())).toList()))
                .toList();
        var services = servicioRepository.findByActivoTrueAndDestacadoTrueOrderByOrdenAscIdAsc().stream()
                .map(item -> new PublicHomeResponse.Servicio(
                        item.getNombre(), item.getSlug(), item.getResumen(), item.getDescripcion(),
                        item.getImagenUrl(), item.getImagenAlt(), item.getEtiqueta(), item.getOrden(),
                        item.getBeneficios().stream().filter(value -> Boolean.TRUE.equals(value.getActivo()))
                                .sorted(BENEFIT_ORDER)
                                .map(value -> value.getTexto()).toList())).toList();
        var projects = proyectoRepository.findByActivoTrueAndDestacadoTrueOrderByOrdenAscIdAsc().stream()
                .map(item -> new PublicHomeResponse.Proyecto(
                        item.getNombre(), item.getSlug(), item.getUbicacion(), item.getFechaProyecto(),
                        item.getDescripcion(), item.getOrden(),
                        item.getImagenUrl() != null
                                ? List.of(new PublicHomeResponse.Imagen(item.getImagenUrl(), item.getImagenAlt(), true, 0))
                                : List.of())).toList();
        PublicHomeResponse.UnidadDestacada highlighted = unidadRepository
                .findByEmpresaIdAndActivoTrueAndDestacadoTrue(site.getEmpresa().getId())
                .map(item -> new PublicHomeResponse.UnidadDestacada(
                        item.getNombre(), item.getSlug(), item.getDescripcion(), item.getIcono(), item.getImagenUrl(),
                        item.getImagenAlt(), item.getOrden(), item.getRecursos().stream()
                                .filter(resource -> Boolean.TRUE.equals(resource.getActivo()))
                                .sorted(RESOURCE_ORDER)
                                .map(resource -> new PublicHomeResponse.Recurso(resource.getTipo(), resource.getUrl(),
                                        resource.getAlt(), resource.getEtiqueta(), resource.getOrden())).toList()))
                .orElse(null);
        return new PublicHomeResponse(sections, services, projects, highlighted);
    }

    @Override @Transactional(readOnly = true)
    public PublicPageResponse findPage(String clave, TipoPaginaPublica pagina) {
        ConfiguracionSitio site = findSiteEntity(clave);
        ContenidoPagina content = contenidoRepository
                .findByConfiguracionSitioIdAndPaginaAndActivoTrue(site.getId(), pagina)
                .orElseThrow(() -> new ModelNotFoundException("Contenido público activo no encontrado para " + pagina));
        SeoPagina seo = seoRepository.findByConfiguracionSitioIdAndTipoPaginaAndUnidadNegocioIsNull(
                site.getId(), TipoPaginaSeo.valueOf(pagina.name())).orElse(null);
        PublicCompanyAboutResponse publicCompany = pagina == TipoPaginaPublica.NOSOTROS
                ? new PublicCompanyAboutResponse(
                        site.getEmpresa().getMision(), site.getEmpresa().getVision(), site.getEmpresa().getValores(),
                        estadisticaEmpresaRepository.findByEmpresaIdAndActivoTrueOrderByOrdenAscIdAsc(
                                site.getEmpresa().getId()).stream()
                                .map(item -> new PublicCompanyStatisticResponse(item.getValor(), item.getPrefijo(),
                                        item.getSufijo(), item.getEtiqueta(), item.getOrden()))
                                .toList())
                : null;
        List<PublicServiceResponse> publicServices = pagina == TipoPaginaPublica.SERVICIOS
                ? servicioRepository.findPublicActiveWithBenefitsOrderByOrdenAscIdAsc().stream()
                        .map(this::toPublicService)
                        .toList()
                : null;
        List<PublicProjectResponse> publicProjects = pagina == TipoPaginaPublica.PROYECTOS
                ? publicProjects()
                : null;
        return new PublicPageResponse(
                new PublicPageResponse.Contenido(content.getPagina(), content.getEyebrow(), content.getTitulo(),
                        content.getIntroduccion(), content.getDescripcion(), content.getImagenUrl(),
                        content.getImagenAlt(), content.getImagenFondoUrl(), List.copyOf(content.getTags())),
                seo == null ? null : new PublicPageResponse.Seo(
                        seo.getTitle(), seo.getDescription(), seo.getOgImageUrl(), seo.getRobots()),
                publicCompany, publicServices, publicProjects);
    }

    @Override @Transactional(readOnly = true)
    public PublicBusinessUnitResponse findBusinessUnit(String clave, String slug) {
        ConfiguracionSitio site = findSiteEntity(clave);
        UnidadNegocio unit = findPublicBusinessUnit(site, slug);
        SeoPagina seo = seoRepository.findByConfiguracionSitioIdAndTipoPaginaAndUnidadNegocioId(
                site.getId(), TipoPaginaSeo.UNIDAD_NEGOCIO, unit.getId()).orElse(null);
        return new PublicBusinessUnitResponse(unit.getNombre(), unit.getSlug(), unit.getDescripcion(), unit.getIcono(),
                unit.getImagenUrl(), unit.getImagenAlt(), unit.getRecursos().stream()
                        .filter(item -> Boolean.TRUE.equals(item.getActivo()))
                        .sorted(RESOURCE_ORDER)
                        .map(item -> new PublicBusinessUnitResponse.Recurso(item.getTipo(), item.getUrl(),
                                item.getAlt(), item.getEtiqueta(), item.getOrden())).toList(),
                seo == null ? null : new PublicBusinessUnitResponse.Seo(
                        seo.getTitle(), seo.getDescription(), seo.getOgImageUrl(), seo.getRobots()));
    }

    @Override @Transactional(readOnly = true)
    public PublicProductCatalogResponse findProductCatalog(String clave, String unidadSlug) {
        ConfiguracionSitio site = findSiteEntity(clave);
        UnidadNegocio unit = findPublicBusinessUnit(site, unidadSlug);
        List<Producto> products = productoRepository.findByUnidadNegocioIdAndEstadoOrderByNombreAscIdAsc(
                unit.getId(), EstadoPublicacion.PUBLICADO);
        List<PublicProductCategoryResponse> categories = products.stream()
                .flatMap(product -> publicCategories(product, unit).stream())
                .distinct()
                .sorted(PRODUCT_CATEGORY_ORDER)
                .map(publicProductMapper::toCategory)
                .toList();
        List<PublicProductCardResponse> cards = products.stream()
                .map(product -> publicProductMapper.toCard(
                        product,
                        primaryImage(product),
                        publicCategories(product, unit).stream().map(publicProductMapper::toCategory).toList()))
                .toList();
        return new PublicProductCatalogResponse(
                new PublicProductCatalogResponse.Unidad(unit.getNombre(), unit.getSlug()), categories, cards);
    }

    @Override @Transactional(readOnly = true)
    public PublicProductDetailResponse findPublicProduct(String clave, String unidadSlug, String productoSlug) {
        ConfiguracionSitio site = findSiteEntity(clave);
        UnidadNegocio unit = findPublicBusinessUnit(site, unidadSlug);
        Producto product = productoRepository.findByUnidadNegocioIdAndSlugAndEstado(
                        unit.getId(), productoSlug, EstadoPublicacion.PUBLICADO)
                .orElseThrow(() -> new ModelNotFoundException("Producto público no encontrado con slug: " + productoSlug));

        // These independent collections are initialized deliberately instead of joining multiple bags.
        product.getImagenes().size();
        product.getVariantes().size();
        product.getEspecificaciones().size();
        product.getDocumentos().size();
        if (product.getConfiguracionCalculo() != null) {
            product.getConfiguracionCalculo().getHabilitada();
        }

        return publicProductMapper.toDetail(
                product,
                publicCategories(product, unit).stream().map(publicProductMapper::toCategory).toList(),
                product.getImagenes().stream().sorted(PRODUCT_IMAGE_ORDER).map(publicProductMapper::toImage).toList(),
                product.getVariantes().stream().sorted(PRODUCT_VARIANT_ORDER).map(publicProductMapper::toVariant).toList(),
                product.getEspecificaciones().stream().sorted(PRODUCT_SPECIFICATION_ORDER)
                        .map(publicProductMapper::toSpecification).toList(),
                product.getDocumentos().stream().sorted(PRODUCT_DOCUMENT_ORDER).map(publicProductMapper::toDocument).toList());
    }

    private ConfiguracionSitio findSiteEntity(String key) {
        return configuracionRepository.findByClave(key)
                .orElseThrow(() -> new ModelNotFoundException("Sitio público no encontrado con clave: " + key));
    }

    private UnidadNegocio findPublicBusinessUnit(ConfiguracionSitio site, String slug) {
        return unidadRepository.findByEmpresaIdAndSlugAndActivoTrue(site.getEmpresa().getId(), slug)
                .orElseThrow(() -> new ModelNotFoundException("Unidad pública activa no encontrada con slug: " + slug));
    }

    private List<CategoriaProducto> publicCategories(Producto product, UnidadNegocio unit) {
        return product.getCategorias().stream()
                .filter(category -> Boolean.TRUE.equals(category.getActivo()))
                .filter(category -> category.getUnidadNegocio() == null
                        || unit.getId().equals(category.getUnidadNegocio().getId()))
                .sorted(PRODUCT_CATEGORY_ORDER)
                .toList();
    }

    private ImagenProducto primaryImage(Producto product) {
        return product.getImagenes().stream().sorted(PRODUCT_IMAGE_ORDER).findFirst().orElse(null);
    }

    private PublicServiceResponse toPublicService(com.isanorte.constructora_api.model.Servicio service) {
        return new PublicServiceResponse(
                service.getNombre(), service.getSlug(), service.getEtiqueta(), service.getResumen(),
                service.getDescripcion(), service.getImagenUrl(), service.getImagenAlt(), service.getOrden(),
                service.getBeneficios().stream()
                        .filter(benefit -> Boolean.TRUE.equals(benefit.getActivo()))
                        .sorted(BENEFIT_ORDER)
                        .map(benefit -> new PublicServiceBenefitResponse(benefit.getTexto(), benefit.getOrden()))
                        .toList());
    }

    private List<PublicProjectResponse> publicProjects() {
        List<com.isanorte.constructora_api.model.Proyecto> projects =
                proyectoRepository.findPublicActiveWithImagesOrderByOrdenAscIdAsc();
        if (!projects.isEmpty()) {
            proyectoRepository.findWithServicesByIdIn(projects.stream()
                    .map(com.isanorte.constructora_api.model.Proyecto::getId)
                    .toList());
        }
        return projects.stream().map(this::toPublicProject).toList();
    }

    private PublicProjectResponse toPublicProject(com.isanorte.constructora_api.model.Proyecto project) {
        return new PublicProjectResponse(
                project.getNombre(), project.getSlug(), project.getDescripcion(), project.getUbicacion(),
                project.getFechaProyecto(), project.getOrden(),
                project.getImagenUrl() != null
                        ? List.of(new PublicProjectImageResponse(project.getImagenUrl(), project.getImagenAlt(), true, 0))
                        : List.of(),
                project.getServicios().stream()
                        .sorted(PROJECT_SERVICE_ORDER)
                        .map(service -> new PublicProjectServiceResponse(service.getNombre(), service.getSlug()))
                        .toList());
    }
}
