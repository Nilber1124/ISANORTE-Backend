package com.isanorte.constructora_api;

import static org.hamcrest.Matchers.startsWith;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import tools.jackson.databind.ObjectMapper;
import com.isanorte.constructora_api.dto.request.PublicCotizacionRequest;
import com.isanorte.constructora_api.dto.request.PublicCotizacionRequest.DetallePublicoRequest;
import com.isanorte.constructora_api.enums.CanalCotizacion;
import com.isanorte.constructora_api.enums.EstadoCotizacion;
import com.isanorte.constructora_api.enums.EstadoDisponibilidad;
import com.isanorte.constructora_api.enums.EstadoPublicacion;
import com.isanorte.constructora_api.model.ConfiguracionSitio;
import com.isanorte.constructora_api.model.Cotizacion;
import com.isanorte.constructora_api.model.Empresa;
import com.isanorte.constructora_api.model.Producto;
import com.isanorte.constructora_api.model.UnidadNegocio;
import com.isanorte.constructora_api.model.VarianteProducto;
import com.isanorte.constructora_api.repository.ConfiguracionSitioRepository;
import com.isanorte.constructora_api.repository.CotizacionRepository;
import com.isanorte.constructora_api.repository.EmpresaRepository;
import com.isanorte.constructora_api.repository.ProductoRepository;
import com.isanorte.constructora_api.repository.UnidadNegocioRepository;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class PublicCotizacionApiIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private EmpresaRepository empresaRepository;
    @Autowired private ConfiguracionSitioRepository configuracionRepository;
    @Autowired private UnidadNegocioRepository unidadRepository;
    @Autowired private ProductoRepository productoRepository;
    @Autowired private CotizacionRepository cotizacionRepository;

    @Test
    void registrarCotizacionPublicaConDetallesYVarianteExitosa() throws Exception {
        ConfiguracionSitio site = site("quote1");
        UnidadNegocio unit = unit(site.getEmpresa(), "isadecor", true);
        Producto product = product(unit, "piso-porcelanato", EstadoPublicacion.PUBLICADO, new BigDecimal("50.00"));
        String varSku = "VAR-" + random(6);
        product.addVariante(VarianteProducto.builder()
                .sku(varSku)
                .nombre("Gris 60x60")
                .precio(new BigDecimal("65.00"))
                .disponible(true)
                .orden(0)
                .build());
        productoRepository.saveAndFlush(product);

        PublicCotizacionRequest request = new PublicCotizacionRequest(
                "Juan Pérez",
                "juan.perez@example.com",
                "+51987654321",
                "Constructora XYZ",
                "Trujillo",
                "Cotización requerida con urgencia",
                CanalCotizacion.FORMULARIO,
                null,
                null,
                null,
                null,
                List.of(new DetallePublicoRequest(product.getSlug(), varSku, 10, "Entrega en obra")));

        var result = mockMvc.perform(post("/api/publico/sitios/{siteKey}/unidades/{unitSlug}/cotizaciones",
                site.getClave(), unit.getSlug())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.codigo", startsWith("COT-")))
                .andExpect(jsonPath("$.estado").value("NUEVA"))
                .andExpect(jsonPath("$.nombreCliente").value("Juan Pérez"))
                .andExpect(jsonPath("$.emailCliente").value("juan.perez@example.com"))
                .andExpect(jsonPath("$.telefonoCliente").value("+51987654321"))
                .andExpect(jsonPath("$.empresaCliente").value("Constructora XYZ"))
                .andExpect(jsonPath("$.ciudad").value("Trujillo"))
                .andExpect(jsonPath("$.mensaje").value("Cotización requerida con urgencia"))
                .andExpect(jsonPath("$.canal").value("FORMULARIO"))
                .andExpect(jsonPath("$.totalEstimado").value(650.00))
                .andExpect(jsonPath("$.detalles.length()").value(1))
                .andExpect(jsonPath("$.detalles[0].productoSlug").value(product.getSlug()))
                .andExpect(jsonPath("$.detalles[0].varianteSku").value(varSku))
                .andExpect(jsonPath("$.detalles[0].cantidad").value(10))
                .andExpect(jsonPath("$.detalles[0].precioUnitario").value(65.00))
                .andExpect(jsonPath("$.detalles[0].subtotal").value(650.00))
                .andExpect(jsonPath("$.detalles[0].notas").value("Entrega en obra"))
                .andExpect(jsonPath("$.fechaCreacion").isNotEmpty())
                .andExpect(jsonPath("$.id").doesNotExist())
                .andExpect(jsonPath("$.detalles[0].id").doesNotExist())
                .andExpect(jsonPath("$.detalles[0].productoId").doesNotExist())
                .andExpect(jsonPath("$.detalles[0].varianteId").doesNotExist())
                .andReturn();

        String responseJson = result.getResponse().getContentAsString();
        String codigo = objectMapper.readTree(responseJson).get("codigo").asText();
        Cotizacion saved = cotizacionRepository.findByCodigo(codigo).orElseThrow();
        assertEquals(EstadoCotizacion.NUEVA, saved.getEstado());
        assertEquals(new BigDecimal("650.00"), saved.getTotalEstimado());
        assertEquals(1, saved.getDetalles().size());
        assertEquals(product.getId(), saved.getDetalles().get(0).getProducto().getId());
        assertNotNull(saved.getDetalles().get(0).getVariante());
    }

    @Test
    void registrarCotizacionPublicaConFormatoSimpleTopLevel() throws Exception {
        ConfiguracionSitio site = site("quote2");
        UnidadNegocio unit = unit(site.getEmpresa(), "isadecor", true);
        Producto product = product(unit, "grifo-monomando", EstadoPublicacion.PUBLICADO, new BigDecimal("120.00"));

        PublicCotizacionRequest request = new PublicCotizacionRequest(
                "Maria Lopez",
                "maria@example.com",
                "987654321",
                null,
                "Lima",
                "Consulta de producto",
                null,
                product.getSlug(),
                null,
                3,
                "Sin variantes",
                null);

        mockMvc.perform(post("/api/publico/sitios/{siteKey}/unidades/{unitSlug}/cotizaciones",
                site.getClave(), unit.getSlug())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.codigo", startsWith("COT-")))
                .andExpect(jsonPath("$.estado").value("NUEVA"))
                .andExpect(jsonPath("$.canal").value("FORMULARIO"))
                .andExpect(jsonPath("$.totalEstimado").value(360.00))
                .andExpect(jsonPath("$.detalles.length()").value(1))
                .andExpect(jsonPath("$.detalles[0].productoSlug").value(product.getSlug()))
                .andExpect(jsonPath("$.detalles[0].varianteSku").isEmpty())
                .andExpect(jsonPath("$.detalles[0].cantidad").value(3))
                .andExpect(jsonPath("$.detalles[0].precioUnitario").value(120.00))
                .andExpect(jsonPath("$.detalles[0].subtotal").value(360.00))
                .andExpect(jsonPath("$.id").doesNotExist());
    }

    @Test
    void rechazaCotizacionSiSitioNoExiste() throws Exception {
        PublicCotizacionRequest request = new PublicCotizacionRequest(
                "Juan Pérez", "juan@example.com", "999888777", null, null, null, null,
                "prod-slug", null, 1, null, null);

        mockMvc.perform(post("/api/publico/sitios/sitio-inexistente/unidades/isadecor/cotizaciones")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void rechazaCotizacionSiUnidadInactivaOPerteneceAOtraEmpresa() throws Exception {
        ConfiguracionSitio site = site("quote-inactive");
        UnidadNegocio inactiveUnit = unit(site.getEmpresa(), "inactiva", false);
        UnidadNegocio foreignUnit = unit(company("other-empresa"), "externa", true);

        PublicCotizacionRequest request = new PublicCotizacionRequest(
                "Juan Pérez", "juan@example.com", "999888777", null, null, null, null,
                "slug", null, 1, null, null);

        mockMvc.perform(post("/api/publico/sitios/{siteKey}/unidades/{unitSlug}/cotizaciones",
                site.getClave(), inactiveUnit.getSlug())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());

        mockMvc.perform(post("/api/publico/sitios/{siteKey}/unidades/{unitSlug}/cotizaciones",
                site.getClave(), foreignUnit.getSlug())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void rechazaCotizacionSiProductoPerteneceAOtraUnidad() throws Exception {
        ConfiguracionSitio site = site("quote-cross-unit");
        UnidadNegocio unitA = unit(site.getEmpresa(), "unit-a", true);
        UnidadNegocio unitB = unit(site.getEmpresa(), "unit-b", true);
        Producto productB = product(unitB, "producto-b", EstadoPublicacion.PUBLICADO, new BigDecimal("100.00"));

        PublicCotizacionRequest request = new PublicCotizacionRequest(
                "Juan Pérez", "juan@example.com", "999888777", null, null, null, null,
                productB.getSlug(), null, 1, null, null);

        mockMvc.perform(post("/api/publico/sitios/{siteKey}/unidades/{unitSlug}/cotizaciones",
                site.getClave(), unitA.getSlug())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void rechazaCotizacionSiProductoNoEstaPublicado() throws Exception {
        ConfiguracionSitio site = site("quote-draft");
        UnidadNegocio unit = unit(site.getEmpresa(), "isadecor", true);
        Producto draft = product(unit, "producto-borrador", EstadoPublicacion.BORRADOR, new BigDecimal("100.00"));

        PublicCotizacionRequest request = new PublicCotizacionRequest(
                "Juan Pérez", "juan@example.com", "999888777", null, null, null, null,
                draft.getSlug(), null, 1, null, null);

        mockMvc.perform(post("/api/publico/sitios/{siteKey}/unidades/{unitSlug}/cotizaciones",
                site.getClave(), unit.getSlug())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void rechazaCotizacionSiVarianteNoPerteneceAlProducto() throws Exception {
        ConfiguracionSitio site = site("quote-invalid-var");
        UnidadNegocio unit = unit(site.getEmpresa(), "isadecor", true);
        Producto product = product(unit, "producto-sin-esa-var", EstadoPublicacion.PUBLICADO, new BigDecimal("100.00"));

        PublicCotizacionRequest request = new PublicCotizacionRequest(
                "Juan Pérez", "juan@example.com", "999888777", null, null, null, null,
                product.getSlug(), "SKU-INEXISTENTE-123", 1, null, null);

        mockMvc.perform(post("/api/publico/sitios/{siteKey}/unidades/{unitSlug}/cotizaciones",
                site.getClave(), unit.getSlug())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void rechazaCotizacionSiFaltanCamposObligatorios() throws Exception {
        ConfiguracionSitio site = site("quote-validation");
        UnidadNegocio unit = unit(site.getEmpresa(), "isadecor", true);

        // Sin emailCliente ni nombreCliente
        String invalidJson = """
                {
                    "nombreCliente": "",
                    "emailCliente": "no-es-email",
                    "telefonoCliente": ""
                }
                """;

        mockMvc.perform(post("/api/publico/sitios/{siteKey}/unidades/{unitSlug}/cotizaciones",
                site.getClave(), unit.getSlug())
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson))
                .andExpect(status().isBadRequest());
    }

    private ConfiguracionSitio site(String seed) {
        Empresa company = company(seed);
        ConfiguracionSitio site = ConfiguracionSitio.builder()
                .clave("site-quote-" + seed + "-" + random(6))
                .tituloSitio("Sitio " + seed)
                .empresa(company)
                .build();
        company.setConfiguracionSitio(site);
        return configuracionRepository.saveAndFlush(site);
    }

    private Empresa company(String seed) {
        return empresaRepository.saveAndFlush(Empresa.builder()
                .razonSocial("Razón " + seed)
                .nombreComercial("Comercial " + seed)
                .ruc("R" + random(19))
                .build());
    }

    private UnidadNegocio unit(Empresa company, String seed, boolean active) {
        return unidadRepository.saveAndFlush(UnidadNegocio.builder()
                .nombre("Unidad " + seed + " " + random(4))
                .slug(seed + "-" + random(4))
                .empresa(company)
                .activo(active)
                .orden(0)
                .build());
    }

    private Producto product(UnidadNegocio unit, String seed, EstadoPublicacion state, BigDecimal precio) {
        Producto product = Producto.builder()
                .sku("SKU-" + seed + "-" + random(5))
                .nombre("Producto " + seed + " " + random(4))
                .slug(seed + "-" + random(5))
                .descripcion("Descripción para cotización")
                .disponibilidad(EstadoDisponibilidad.DISPONIBLE)
                .estado(state)
                .precioBase(precio)
                .build();
        unit.addProducto(product);
        return productoRepository.saveAndFlush(product);
    }

    private String random(int length) {
        return UUID.randomUUID().toString().replace("-", "").substring(0, length);
    }
}
