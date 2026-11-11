package com.isanorte.constructora_api;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.UUID;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.transaction.annotation.Transactional;

import com.isanorte.constructora_api.enums.EstadoDisponibilidad;
import com.isanorte.constructora_api.enums.EstadoPublicacion;
import com.isanorte.constructora_api.enums.TipoDocumentoProducto;
import com.isanorte.constructora_api.model.CategoriaProducto;
import com.isanorte.constructora_api.model.ConfiguracionCalculo;
import com.isanorte.constructora_api.model.ConfiguracionSitio;
import com.isanorte.constructora_api.model.DocumentoProducto;
import com.isanorte.constructora_api.model.Empresa;
import com.isanorte.constructora_api.model.EspecificacionProducto;
import com.isanorte.constructora_api.model.ImagenProducto;
import com.isanorte.constructora_api.model.Producto;
import com.isanorte.constructora_api.model.ResenaProducto;
import com.isanorte.constructora_api.model.Cliente;
import com.isanorte.constructora_api.model.UnidadNegocio;
import com.isanorte.constructora_api.model.VarianteProducto;
import com.isanorte.constructora_api.repository.CategoriaProductoRepository;
import com.isanorte.constructora_api.repository.ConfiguracionSitioRepository;
import com.isanorte.constructora_api.repository.EmpresaRepository;
import com.isanorte.constructora_api.repository.ProductoRepository;
import com.isanorte.constructora_api.repository.ResenaProductoRepository;
import com.isanorte.constructora_api.repository.ClienteRepository;
import com.isanorte.constructora_api.repository.UnidadNegocioRepository;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class PublicProductApiIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private EmpresaRepository empresaRepository;
    @Autowired private ConfiguracionSitioRepository configuracionRepository;
    @Autowired private UnidadNegocioRepository unidadRepository;
    @Autowired private CategoriaProductoRepository categoriaRepository;
    @Autowired private ProductoRepository productoRepository;
    @Autowired private ResenaProductoRepository resenaRepository;
    @Autowired private ClienteRepository clienteRepository;
    @Autowired private JwtEncoder jwtEncoder;

    @Test
    void recomendacionesYResenasPublicasUsanProductosYDatosPersistidos() throws Exception {
        ConfiguracionSitio site = site("reviews");
        UnidadNegocio unit = unit(site.getEmpresa(), "reviews-unit", true);
        CategoriaProducto category = category("reviews-category", unit, true, 0);
        Producto current = product(unit, "reviews-current", EstadoPublicacion.PUBLICADO);
        current.addCategoria(category);
        productoRepository.saveAndFlush(current);
        Producto related = product(unit, "reviews-related", EstadoPublicacion.PUBLICADO);
        related.addCategoria(category);
        productoRepository.saveAndFlush(related);
        Producto fallback = product(unit, "reviews-fallback", EstadoPublicacion.PUBLICADO);
        product(unit, "reviews-draft", EstadoPublicacion.BORRADOR);

        ResenaProducto relatedReview = resenaRepository.saveAndFlush(ResenaProducto.builder()
                .producto(related).clienteId(UUID.randomUUID()).nombreCliente("Cliente relacionado")
                .calificacion(5).comentario("Excelente alternativa").compraVerificada(true).build());
        ResenaProducto currentReview = resenaRepository.saveAndFlush(ResenaProducto.builder()
                .producto(current).clienteId(UUID.randomUUID()).nombreCliente("Cliente ISADECOR")
                .calificacion(4).titulo("Buen acabado").comentario("La instalación fue sencilla.").build());

        String productPath = "/api/publico/sitios/{key}/unidades/{unit}/productos/{product}";
        mockMvc.perform(get(productPath + "/recomendados", site.getClave(), unit.getSlug(), current.getSlug()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].slug").value(related.getSlug()))
                .andExpect(jsonPath("$[0].calificacionPromedio").value(5.0))
                .andExpect(jsonPath("$[*].slug", not(hasItem(current.getSlug()))))
                .andExpect(jsonPath("$[*].slug", hasItem(fallback.getSlug())))
                .andExpect(jsonPath("$.length()").value(2));

        mockMvc.perform(get(productPath + "/resenas", site.getClave(), unit.getSlug(), current.getSlug()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombreCliente").value("Cliente ISADECOR"))
                .andExpect(jsonPath("$[0].calificacion").value(4));
        mockMvc.perform(get(productPath + "/resenas/resumen", site.getClave(), unit.getSlug(), current.getSlug()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.promedio").value(4.0))
                .andExpect(jsonPath("$.total").value(1))
                .andExpect(jsonPath("$.distribucion[1].calificacion").value(4))
                .andExpect(jsonPath("$.distribucion[1].cantidad").value(1));
        mockMvc.perform(post("/api/publico/resenas/{id}/util", currentReview.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cantidadUtil").value(1));
        mockMvc.perform(post(productPath + "/resenas", site.getClave(), unit.getSlug(), current.getSlug())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"calificacion\":5,\"comentario\":\"Excelente\"}"))
                .andExpect(status().isUnauthorized());

        Cliente cliente = clienteRepository.saveAndFlush(Cliente.builder()
                .nombre("Ana").apellido("Cliente").email("ana-" + random(6) + "@example.com")
                .passwordHash("hash-no-expuesto").activo(true).consentimientoDatos(true)
                .fechaConsentimiento(LocalDateTime.now()).build());
        String token = createToken(cliente.getId().toString(), Set.of("CLIENTE"));
        mockMvc.perform(post(productPath + "/resenas", site.getClave(), unit.getSlug(), current.getSlug())
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"calificacion\":5,\"comentario\":\"Excelente\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombreCliente").value("Ana Cliente"))
                .andExpect(jsonPath("$.calificacion").value(5));
        org.junit.jupiter.api.Assertions.assertTrue(resenaRepository.findByProductoId(current.getId()).stream()
                .anyMatch(review -> cliente.getId().equals(review.getClienteId())));

        org.junit.jupiter.api.Assertions.assertNotNull(relatedReview.getId());
    }

    @Test
    void catalogoPublicoSeAcotaALaUnidadYSoloExponeCategoriasValidas() throws Exception {
        ConfiguracionSitio site = site("catalogo");
        UnidadNegocio unitA = unit(site.getEmpresa(), "catalog-a", true);
        UnidadNegocio unitB = unit(site.getEmpresa(), "catalog-b", true);
        UnidadNegocio inactive = unit(site.getEmpresa(), "catalog-inactive", false);
        UnidadNegocio foreign = unit(company("foreign"), "catalog-foreign", true);

        CategoriaProducto global = category("global", null, true, 1);
        CategoriaProducto scopedA = category("scoped-a", unitA, true, 0);
        CategoriaProducto scopedB = category("scoped-b", unitB, true, 0);
        CategoriaProducto inactiveCategory = category("inactive", unitA, false, 2);
        Producto productA = product(unitA, "alpha", EstadoPublicacion.PUBLICADO);
        productA.addCategoria(global);
        productA.addCategoria(scopedA);
        productA.addCategoria(scopedB); // inconsistent legacy association: must not be public under A.
        productA.addCategoria(inactiveCategory);
        productA.addImagen(ImagenProducto.builder().url("/secondary.webp").altText(null)
                .esPrincipal(false).orden(0).build());
        productA.addImagen(ImagenProducto.builder().url("/cover.webp").altText("Portada").esPrincipal(true)
                .orden(9).build());
        productoRepository.saveAndFlush(productA);
        Producto laterProductA = product(unitA, "zeta", EstadoPublicacion.PUBLICADO);
        Producto draftA = product(unitA, "draft", EstadoPublicacion.BORRADOR);
        Producto productB = product(unitB, "bravo", EstadoPublicacion.PUBLICADO);
        productB.addCategoria(scopedB);
        productoRepository.saveAndFlush(productB);

        mockMvc.perform(get("/api/publico/sitios/{key}/unidades/{unit}/catalogo", site.getClave(), unitA.getSlug()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.unidad.nombre").value(unitA.getNombre()))
                .andExpect(jsonPath("$.productos.totalElementos").value(2))
                .andExpect(jsonPath("$.productos.contenido.length()").value(2))
                .andExpect(jsonPath("$.productos.contenido[0].slug").value(productA.getSlug()))
                .andExpect(jsonPath("$.productos.contenido[1].slug").value(laterProductA.getSlug()))
                .andExpect(jsonPath("$.productos.contenido[0].imagen.url").value("/cover.webp"))
                .andExpect(jsonPath("$.productos.contenido[0].categorias[*].slug", hasItem(global.getSlug())))
                .andExpect(jsonPath("$.productos.contenido[0].categorias[*].slug", hasItem(scopedA.getSlug())))
                .andExpect(jsonPath("$.productos.contenido[0].categorias[*].slug", not(hasItem(scopedB.getSlug()))))
                .andExpect(jsonPath("$.categorias[*].slug", not(hasItem(inactiveCategory.getSlug()))))
                .andExpect(jsonPath("$.productos.contenido[*].slug", not(hasItem(draftA.getSlug()))))
                .andExpect(jsonPath("$.productos.contenido[*].slug", not(hasItem(productB.getSlug()))))
                .andExpect(jsonPath("$.productos.contenido[0].id").doesNotExist())
                .andExpect(jsonPath("$.productos.contenido[0].estado").doesNotExist())
                .andExpect(jsonPath("$.productos.contenido[0].destacado").value(false))
                .andExpect(jsonPath("$.productos.contenido[0].ordenDestacado").value(0))
                .andExpect(jsonPath("$.productos.contenido[0].fechaCreacion").doesNotExist())
                .andExpect(jsonPath("$.productos.contenido[0].unidadNegocioId").doesNotExist());

        mockMvc.perform(get("/api/publico/sitios/{key}/unidades/{unit}/catalogo", site.getClave(), inactive.getSlug()))
                .andExpect(status().isNotFound());
        mockMvc.perform(get("/api/publico/sitios/{key}/unidades/{unit}/catalogo", site.getClave(), foreign.getSlug()))
                .andExpect(status().isNotFound());
        mockMvc.perform(get("/api/publico/sitios/{key}/unidades/missing/catalogo", site.getClave()))
                .andExpect(status().isNotFound());

        UnidadNegocio empty = unit(site.getEmpresa(), "catalog-empty", true);
        mockMvc.perform(get("/api/publico/sitios/{key}/unidades/{unit}/catalogo", site.getClave(), empty.getSlug()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productos.contenido").isArray())
                .andExpect(jsonPath("$.productos.contenido.length()").value(0))
                .andExpect(jsonPath("$.productos.totalElementos").value(0))
                .andExpect(jsonPath("$.categorias.length()").value(0));
    }

    @Test
    void catalogoPublicoPaginaFiltraYOrdenaDesdeElBackend() throws Exception {
        ConfiguracionSitio site = site("catalogo-paginado");
        UnidadNegocio unit = unit(site.getEmpresa(), "catalog-page", true);
        CategoriaProducto mural = category("mural", unit, true, 0);
        CategoriaProducto piso = category("piso", unit, true, 1);

        Producto barato = product(unit, "panel-economico", EstadoPublicacion.PUBLICADO);
        barato.setPrecioBase(new BigDecimal("15.00"));
        barato.setDisponibilidad(EstadoDisponibilidad.AGOTADO);
        barato.addCategoria(mural);
        productoRepository.saveAndFlush(barato);

        Producto caro = product(unit, "panel-premium", EstadoPublicacion.PUBLICADO);
        caro.setPrecioBase(new BigDecimal("500.00"));
        caro.setDescuentoPorcentaje(new BigDecimal("10.00"));
        caro.setDisponibilidad(EstadoDisponibilidad.DISPONIBLE);
        caro.addCategoria(mural);
        productoRepository.saveAndFlush(caro);

        Producto sinPrecio = product(unit, "panel-a-consultar", EstadoPublicacion.PUBLICADO);
        sinPrecio.setPrecioBase(null);
        sinPrecio.setDisponibilidad(EstadoDisponibilidad.DISPONIBLE);
        sinPrecio.addCategoria(piso);
        productoRepository.saveAndFlush(sinPrecio);

        String basePath = "/api/publico/sitios/{key}/unidades/{unit}/catalogo";

        // Paginación: size=1 trae solo un elemento por página, pero el total refleja todo el catálogo.
        mockMvc.perform(get(basePath, site.getClave(), unit.getSlug()).param("page", "0").param("size", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productos.contenido.length()").value(1))
                .andExpect(jsonPath("$.productos.totalElementos").value(3))
                .andExpect(jsonPath("$.productos.totalPaginas").value(3))
                .andExpect(jsonPath("$.productos.pagina").value(0));

        // Precio ascendente con nulls al final.
        mockMvc.perform(get(basePath, site.getClave(), unit.getSlug())
                        .param("orden", "PRECIO_ASC").param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productos.contenido[0].slug").value(barato.getSlug()))
                .andExpect(jsonPath("$.productos.contenido[1].slug").value(caro.getSlug()))
                .andExpect(jsonPath("$.productos.contenido[2].slug").value(sinPrecio.getSlug()));

        // Precio descendente también deja los nulls al final (no primero, como haría el default de Postgres).
        mockMvc.perform(get(basePath, site.getClave(), unit.getSlug())
                        .param("orden", "PRECIO_DESC").param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productos.contenido[0].slug").value(caro.getSlug()))
                .andExpect(jsonPath("$.productos.contenido[1].slug").value(barato.getSlug()))
                .andExpect(jsonPath("$.productos.contenido[2].slug").value(sinPrecio.getSlug()));

        // Filtro por categoría.
        mockMvc.perform(get(basePath, site.getClave(), unit.getSlug())
                        .param("categoria", piso.getSlug()).param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productos.contenido.length()").value(1))
                .andExpect(jsonPath("$.productos.contenido[0].slug").value(sinPrecio.getSlug()));

        // Búsqueda de texto por nombre.
        mockMvc.perform(get(basePath, site.getClave(), unit.getSlug())
                        .param("q", "premium").param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productos.contenido.length()").value(1))
                .andExpect(jsonPath("$.productos.contenido[0].slug").value(caro.getSlug()));

        // Búsqueda sin coincidencias.
        mockMvc.perform(get(basePath, site.getClave(), unit.getSlug())
                        .param("q", "inexistente-xyz").param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productos.contenido.length()").value(0))
                .andExpect(jsonPath("$.productos.totalElementos").value(0));

        // Solo descuento / solo disponible.
        mockMvc.perform(get(basePath, site.getClave(), unit.getSlug())
                        .param("soloDescuento", "true").param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productos.contenido.length()").value(1))
                .andExpect(jsonPath("$.productos.contenido[0].slug").value(caro.getSlug()));
        mockMvc.perform(get(basePath, site.getClave(), unit.getSlug())
                        .param("soloDisponible", "true").param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productos.contenido.length()").value(2))
                .andExpect(jsonPath("$.productos.contenido[*].slug", not(hasItem(barato.getSlug()))));

        // El conteo de categorías (facet) no cambia aunque haya un filtro activo.
        mockMvc.perform(get(basePath, site.getClave(), unit.getSlug())
                        .param("categoria", piso.getSlug()).param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.categorias[?(@.slug=='" + mural.getSlug() + "')].cantidad").value(hasItem(2)))
                .andExpect(jsonPath("$.categorias[?(@.slug=='" + piso.getSlug() + "')].cantidad").value(hasItem(1)));

        // Página fuera de rango: 200 con contenido vacío, no error.
        mockMvc.perform(get(basePath, site.getClave(), unit.getSlug()).param("page", "99").param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productos.contenido.length()").value(0))
                .andExpect(jsonPath("$.productos.totalElementos").value(3));

        // Parámetros de paginación inválidos.
        mockMvc.perform(get(basePath, site.getClave(), unit.getSlug()).param("page", "-1"))
                .andExpect(status().isBadRequest());
        mockMvc.perform(get(basePath, site.getClave(), unit.getSlug()).param("size", "0"))
                .andExpect(status().isBadRequest());
        mockMvc.perform(get(basePath, site.getClave(), unit.getSlug()).param("size", "49"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void detallePublicoValidaOwnershipYExponeSoloElAgregadoPresentable() throws Exception {
        ConfiguracionSitio site = site("detail");
        UnidadNegocio unitA = unit(site.getEmpresa(), "detail-a", true);
        UnidadNegocio unitB = unit(site.getEmpresa(), "detail-b", true);
        Producto productA = product(unitA, "detail-alpha", EstadoPublicacion.PUBLICADO);
        productA.setTituloSeo("Wall Panel SEO | ISADECOR");
        productA.setDescripcionSeo("Descripción SEO para Wall Panel");
        productA.setRetiroEnTienda(true);
        CategoriaProducto global = category("detail-global", null, true, 0);
        productA.addCategoria(global);
        productA.addImagen(ImagenProducto.builder().url("/secondary.webp").altText(null)
                .esPrincipal(false).orden(0).build());
        productA.addImagen(ImagenProducto.builder().url("/principal.webp").altText("Principal").esPrincipal(true)
                .orden(5).build());
        productA.addVariante(VarianteProducto.builder().sku("VAR-" + random(8)).nombre("Variante")
                .precio(new BigDecimal("12.50")).disponible(true).orden(0).build());
        productA.addEspecificacion(EspecificacionProducto.builder().clave("Formato").valor("60x60")
                .grupo("Técnica").orden(0).build());
        productA.addDocumento(DocumentoProducto.builder().titulo("Ficha").url("/ficha.pdf")
                .tipo(TipoDocumentoProducto.FICHA_TECNICA).formato("PDF").tamanoBytes(128L).build());
        productA.setConfiguracionCalculo(ConfiguracionCalculo.builder().habilitada(true).etiquetaEntrada("Área")
                .unidadEntrada("m2").coberturaPorUnidad(new BigDecimal("1.2500")).unidadVenta("caja")
                .textoAyuda("Calcula cajas").producto(productA).build());
        productoRepository.saveAndFlush(productA);
        Producto productB = product(unitB, "detail-bravo", EstadoPublicacion.PUBLICADO);
        Producto draftA = product(unitA, "detail-draft", EstadoPublicacion.BORRADOR);
        Producto withoutChildren = product(unitA, "detail-empty", EstadoPublicacion.PUBLICADO);
        UnidadNegocio inactive = unit(site.getEmpresa(), "detail-inactive", false);

        mockMvc.perform(get("/api/publico/sitios/{key}/unidades/{unit}/productos/{product}",
                site.getClave(), unitA.getSlug(), productA.getSlug()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.slug").value(productA.getSlug()))
                .andExpect(jsonPath("$.tituloSeo").value("Wall Panel SEO | ISADECOR"))
                .andExpect(jsonPath("$.descripcionSeo").value("Descripción SEO para Wall Panel"))
                .andExpect(jsonPath("$.retiroEnTienda").value(true))
                .andExpect(jsonPath("$.imagenes[0].url").value("/principal.webp"))
                .andExpect(jsonPath("$.imagenes[1].url").value("/secondary.webp"))
                .andExpect(jsonPath("$.variantes[0].orden").value(0))
                .andExpect(jsonPath("$.especificaciones[0].clave").value("Formato"))
                .andExpect(jsonPath("$.documentos[0].tipo").value("FICHA_TECNICA"))
                .andExpect(jsonPath("$.configuracionCalculo.coberturaPorUnidad").value(1.25))
                .andExpect(jsonPath("$.id").doesNotExist())
                .andExpect(jsonPath("$.estado").doesNotExist())
                .andExpect(jsonPath("$.imagenes[0].id").doesNotExist())
                .andExpect(jsonPath("$.variantes[0].id").doesNotExist())
                .andExpect(jsonPath("$.configuracionCalculo.id").doesNotExist());

        mockMvc.perform(get("/api/publico/sitios/{key}/unidades/{unit}/productos/{product}",
                site.getClave(), unitA.getSlug(), withoutChildren.getSlug()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.imagenes.length()").value(0))
                .andExpect(jsonPath("$.variantes.length()").value(0))
                .andExpect(jsonPath("$.especificaciones.length()").value(0))
                .andExpect(jsonPath("$.documentos.length()").value(0))
                .andExpect(jsonPath("$.configuracionCalculo").value(org.hamcrest.Matchers.nullValue()));

        mockMvc.perform(get("/api/publico/sitios/{key}/unidades/{unit}/productos/{product}",
                site.getClave(), unitA.getSlug(), productB.getSlug())).andExpect(status().isNotFound());
        mockMvc.perform(get("/api/publico/sitios/{key}/unidades/{unit}/productos/{product}",
                site.getClave(), unitB.getSlug(), productB.getSlug())).andExpect(status().isOk());
        mockMvc.perform(get("/api/publico/sitios/{key}/unidades/{unit}/productos/{product}",
                site.getClave(), unitA.getSlug(), draftA.getSlug())).andExpect(status().isNotFound());
        mockMvc.perform(get("/api/publico/sitios/{key}/unidades/missing/productos/{product}",
                site.getClave(), productA.getSlug())).andExpect(status().isNotFound());
        mockMvc.perform(get("/api/publico/sitios/{key}/unidades/{unit}/productos/{product}",
                site.getClave(), inactive.getSlug(), productA.getSlug())).andExpect(status().isNotFound());
        mockMvc.perform(get("/api/publico/sitios/{key}/unidades/{unit}/productos/missing", site.getClave(),
                unitA.getSlug())).andExpect(status().isNotFound());
    }

    private ConfiguracionSitio site(String seed) {
        Empresa company = company(seed);
        ConfiguracionSitio site = ConfiguracionSitio.builder().clave("site-product-" + seed + "-" + random(6))
                .tituloSitio("Sitio " + seed).empresa(company).build();
        company.setConfiguracionSitio(site);
        return configuracionRepository.saveAndFlush(site);
    }

    private Empresa company(String seed) {
        return empresaRepository.saveAndFlush(Empresa.builder().razonSocial("Razón " + seed)
                .nombreComercial("Comercial " + seed).ruc("R" + random(19)).build());
    }

    private UnidadNegocio unit(Empresa company, String seed, boolean active) {
        return unidadRepository.saveAndFlush(UnidadNegocio.builder().nombre("Unidad " + seed + random(4))
                .slug(seed + "-" + random(4)).empresa(company).activo(active).orden(0).build());
    }

    private CategoriaProducto category(String seed, UnidadNegocio unit, boolean active, int order) {
        return categoriaRepository.saveAndFlush(CategoriaProducto.builder().nombre("Categoría " + seed + random(4))
                .slug(seed + "-" + random(5)).unidadNegocio(unit).activo(active).orden(order).build());
    }

    private Producto product(UnidadNegocio unit, String seed, EstadoPublicacion state) {
        Producto product = Producto.builder().sku("SKU-" + seed + "-" + random(5))
                .nombre("Producto " + seed + random(4)).slug(seed + "-" + random(5))
                .descripcion("Descripción pública").disponibilidad(EstadoDisponibilidad.DISPONIBLE)
                .estado(state).precioBase(new BigDecimal("10.00")).build();
        unit.addProducto(product);
        return productoRepository.saveAndFlush(product);
    }

    private String random(int length) {
        return UUID.randomUUID().toString().replace("-", "").substring(0, length);
    }

    private String createToken(String subject, Set<String> roles) {
        Instant now = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder().issuer("isanorte-api").subject(subject)
                .issuedAt(now).expiresAt(now.plusSeconds(3600)).claim("roles", roles).build();
        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).type("JWT").build();
        return jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
    }
}
