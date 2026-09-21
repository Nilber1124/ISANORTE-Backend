package com.isanorte.constructora_api;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
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
import com.isanorte.constructora_api.model.UnidadNegocio;
import com.isanorte.constructora_api.model.VarianteProducto;
import com.isanorte.constructora_api.repository.CategoriaProductoRepository;
import com.isanorte.constructora_api.repository.ConfiguracionSitioRepository;
import com.isanorte.constructora_api.repository.EmpresaRepository;
import com.isanorte.constructora_api.repository.ProductoRepository;
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
                .andExpect(jsonPath("$.productos.length()").value(2))
                .andExpect(jsonPath("$.productos[0].slug").value(productA.getSlug()))
                .andExpect(jsonPath("$.productos[1].slug").value(laterProductA.getSlug()))
                .andExpect(jsonPath("$.productos[0].imagen.url").value("/cover.webp"))
                .andExpect(jsonPath("$.productos[0].categorias[*].slug", hasItem(global.getSlug())))
                .andExpect(jsonPath("$.productos[0].categorias[*].slug", hasItem(scopedA.getSlug())))
                .andExpect(jsonPath("$.productos[0].categorias[*].slug", not(hasItem(scopedB.getSlug()))))
                .andExpect(jsonPath("$.categorias[*].slug", not(hasItem(inactiveCategory.getSlug()))))
                .andExpect(jsonPath("$.productos[*].slug", not(hasItem(draftA.getSlug()))))
                .andExpect(jsonPath("$.productos[*].slug", not(hasItem(productB.getSlug()))))
                .andExpect(jsonPath("$.productos[0].id").doesNotExist())
                .andExpect(jsonPath("$.productos[0].estado").doesNotExist())
                .andExpect(jsonPath("$.productos[0].destacado").doesNotExist())
                .andExpect(jsonPath("$.productos[0].fechaCreacion").doesNotExist())
                .andExpect(jsonPath("$.productos[0].unidadNegocioId").doesNotExist());

        mockMvc.perform(get("/api/publico/sitios/{key}/unidades/{unit}/catalogo", site.getClave(), inactive.getSlug()))
                .andExpect(status().isNotFound());
        mockMvc.perform(get("/api/publico/sitios/{key}/unidades/{unit}/catalogo", site.getClave(), foreign.getSlug()))
                .andExpect(status().isNotFound());
        mockMvc.perform(get("/api/publico/sitios/{key}/unidades/missing/catalogo", site.getClave()))
                .andExpect(status().isNotFound());

        UnidadNegocio empty = unit(site.getEmpresa(), "catalog-empty", true);
        mockMvc.perform(get("/api/publico/sitios/{key}/unidades/{unit}/catalogo", site.getClave(), empty.getSlug()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productos").isArray())
                .andExpect(jsonPath("$.productos.length()").value(0))
                .andExpect(jsonPath("$.categorias.length()").value(0));
    }

    @Test
    void detallePublicoValidaOwnershipYExponeSoloElAgregadoPresentable() throws Exception {
        ConfiguracionSitio site = site("detail");
        UnidadNegocio unitA = unit(site.getEmpresa(), "detail-a", true);
        UnidadNegocio unitB = unit(site.getEmpresa(), "detail-b", true);
        Producto productA = product(unitA, "detail-alpha", EstadoPublicacion.PUBLICADO);
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
}
