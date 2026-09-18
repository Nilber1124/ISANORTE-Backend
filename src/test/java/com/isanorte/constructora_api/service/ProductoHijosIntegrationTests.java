package com.isanorte.constructora_api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

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
import com.isanorte.constructora_api.enums.EstadoDisponibilidad;
import com.isanorte.constructora_api.enums.EstadoPublicacion;
import com.isanorte.constructora_api.enums.TipoDocumentoProducto;
import com.isanorte.constructora_api.exception.ModelNotFoundException;
import com.isanorte.constructora_api.model.Empresa;
import com.isanorte.constructora_api.model.Producto;
import com.isanorte.constructora_api.model.UnidadNegocio;
import com.isanorte.constructora_api.repository.DocumentoProductoRepository;
import com.isanorte.constructora_api.repository.EmpresaRepository;
import com.isanorte.constructora_api.repository.EspecificacionProductoRepository;
import com.isanorte.constructora_api.repository.ImagenProductoRepository;
import com.isanorte.constructora_api.repository.ProductoRepository;
import com.isanorte.constructora_api.repository.UnidadNegocioRepository;
import com.isanorte.constructora_api.repository.VarianteProductoRepository;

import jakarta.validation.Validator;
import com.jayway.jsonpath.JsonPath;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
class ProductoHijosIntegrationTests {

    @Autowired
    private IProductoService productoService;
    @Autowired
    private ProductoRepository productoRepository;
    @Autowired
    private VarianteProductoRepository varianteRepository;
    @Autowired
    private ImagenProductoRepository imagenRepository;
    @Autowired
    private EspecificacionProductoRepository especificacionRepository;
    @Autowired
    private DocumentoProductoRepository documentoRepository;
    @Autowired
    private EmpresaRepository empresaRepository;
    @Autowired
    private UnidadNegocioRepository unidadRepository;
    @Autowired
    private Validator validator;
    @Autowired
    private MockMvc mockMvc;

    private Producto producto;

    @BeforeEach
    void setUp() {
        producto = crearProducto("wall-panel-roble", EstadoPublicacion.PUBLICADO);
    }

    @Test
    void creaEditaYEliminaVarianteRespetandoOrphanRemoval() {
        VarianteProductoResponse creada = productoService.createVariante(producto.getId(),
                new VarianteProductoRequest("WPR-NAT", "Natural", null, new BigDecimal("45.90"), true, null, 1));

        VarianteProductoResponse editada = productoService.updateVariante(producto.getId(), creada.id(),
                new VarianteProductoRequest("WPR-NAT-2", "Natural claro", "Nuevo tono",
                        new BigDecimal("49.90"), false, "https://example.test/natural.jpg", 2));

        assertThat(editada.nombre()).isEqualTo("Natural claro");
        assertThat(varianteRepository.findById(creada.id()).orElseThrow().getProducto().getId())
                .isEqualTo(producto.getId());

        productoService.deleteVariante(producto.getId(), creada.id());

        assertThat(varianteRepository.findById(creada.id())).isEmpty();
    }

    @Test
    void rechazaSkuDuplicadoDeVarianteConConflictoDeDominio() {
        productoService.createVariante(producto.getId(),
                new VarianteProductoRequest("WPR-DUP", "Primera", null, null, true, null, 0));

        assertThatThrownBy(() -> productoService.createVariante(producto.getId(),
                new VarianteProductoRequest("WPR-DUP", "Segunda", null, null, true, null, 0)))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("SKU");
    }

    @Test
    void creaEditaYEliminaImagenSinInventarUnicidadDePrincipal() {
        ImagenProductoResponse primera = productoService.createImagen(producto.getId(),
                new ImagenProductoRequest("https://example.test/1.jpg", "Frontal", true, 1));
        ImagenProductoResponse segunda = productoService.createImagen(producto.getId(),
                new ImagenProductoRequest("https://example.test/2.jpg", "Detalle", true, 2));

        ImagenProductoResponse editada = productoService.updateImagen(producto.getId(), primera.id(),
                new ImagenProductoRequest("https://example.test/1b.jpg", "Frontal editada", false, 3));

        assertThat(editada.esPrincipal()).isFalse();
        assertThat(imagenRepository.findById(segunda.id()).orElseThrow().getEsPrincipal()).isTrue();

        productoService.deleteImagen(producto.getId(), primera.id());
        assertThat(imagenRepository.findById(primera.id())).isEmpty();
    }

    @Test
    void creaEditaYEliminaEspecificacionYLosGetsReflejanLosCambios() {
        EspecificacionProductoResponse creada = productoService.createEspecificacion(producto.getId(),
                new EspecificacionProductoRequest("Espesor", "8 mm", "Dimensiones", 1));

        EspecificacionProductoResponse editada = productoService.updateEspecificacion(producto.getId(), creada.id(),
                new EspecificacionProductoRequest("Espesor", "9 mm", "Medidas", 2));

        ProductoResponse administrativo = productoService.findByIdResponse(producto.getId());
        ProductoResponse publico = productoService.findPublishedBySlugResponse(producto.getSlug());
        assertThat(editada.valor()).isEqualTo("9 mm");
        assertThat(administrativo.especificaciones()).extracting(EspecificacionProductoResponse::id)
                .contains(creada.id());
        assertThat(publico.especificaciones()).extracting(EspecificacionProductoResponse::valor)
                .contains("9 mm");

        productoService.deleteEspecificacion(producto.getId(), creada.id());
        assertThat(especificacionRepository.findById(creada.id())).isEmpty();
    }

    @Test
    void creaEditaYEliminaDocumentoConElEnumReal() {
        DocumentoProductoResponse creado = productoService.createDocumento(producto.getId(),
                new DocumentoProductoRequest("Ficha", "https://example.test/ficha.pdf",
                        TipoDocumentoProducto.FICHA_TECNICA, "PDF", 1024L));

        DocumentoProductoResponse editado = productoService.updateDocumento(producto.getId(), creado.id(),
                new DocumentoProductoRequest("Manual", "https://example.test/manual.pdf",
                        TipoDocumentoProducto.MANUAL, "PDF", 2048L));

        assertThat(editado.tipo()).isEqualTo(TipoDocumentoProducto.MANUAL);
        productoService.deleteDocumento(producto.getId(), creado.id());
        assertThat(documentoRepository.findById(creado.id())).isEmpty();
    }

    @Test
    void creaActualizaYDeshabilitaConfiguracionCalculo() {
        ConfiguracionCalculoResponse creada = productoService.upsertConfiguracionCalculo(producto.getId(),
                new ConfiguracionCalculoRequest(true, "Área", "m2", new BigDecimal("2.40"), "caja", "Redondea"));

        ConfiguracionCalculoResponse actualizada = productoService.upsertConfiguracionCalculo(producto.getId(),
                new ConfiguracionCalculoRequest(false, "Área útil", "m2", new BigDecimal("2.50"), "caja", null));

        assertThat(actualizada.id()).isEqualTo(creada.id());
        assertThat(actualizada.habilitada()).isFalse();
        assertThat(productoService.findPublishedBySlugResponse(producto.getSlug()).configuracionCalculo()
                .coberturaPorUnidad()).isEqualByComparingTo("2.50");
    }

    @Test
    void informaProductoEHijoInexistentes() {
        UUID inexistente = UUID.randomUUID();

        assertThatThrownBy(() -> productoService.createImagen(inexistente,
                new ImagenProductoRequest("https://example.test/a.jpg", "A", false, 0)))
                .isInstanceOf(ModelNotFoundException.class)
                .hasMessageContaining("Producto");
        assertThatThrownBy(() -> productoService.deleteImagen(producto.getId(), inexistente))
                .isInstanceOf(ModelNotFoundException.class)
                .hasMessageContaining("Imagen");
    }

    @Test
    void rechazaManipularUnHijoDeOtroProducto() {
        Producto otro = crearProducto("otro-producto", EstadoPublicacion.BORRADOR);
        ImagenProductoResponse imagenAjena = productoService.createImagen(otro.getId(),
                new ImagenProductoRequest("https://example.test/ajena.jpg", "Ajena", false, 0));

        assertThatThrownBy(() -> productoService.updateImagen(producto.getId(), imagenAjena.id(),
                new ImagenProductoRequest("https://example.test/ataque.jpg", "Ataque", false, 0)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("no pertenece");
        assertThat(imagenRepository.findById(imagenAjena.id()).orElseThrow().getProducto().getId())
                .isEqualTo(otro.getId());
    }

    @Test
    void validaCamposObligatoriosLongitudesYCoberturaPositiva() {
        VarianteProductoRequest varianteInvalida = new VarianteProductoRequest(" ", "Nombre", null, null, null,
                null, null);
        ConfiguracionCalculoRequest configuracionInvalida = new ConfiguracionCalculoRequest(
                true, null, null, BigDecimal.ZERO, null, null);

        assertThat(validator.validate(varianteInvalida)).extracting(violation -> violation.getPropertyPath().toString())
                .contains("sku", "disponible", "orden");
        assertThat(validator.validate(configuracionInvalida)).extracting(violation -> violation.getPropertyPath().toString())
                .contains("coberturaPorUnidad");
    }

    @Test
    void putPrincipalPreservaTodosLosHijos() {
        VarianteProductoResponse variante = productoService.createVariante(producto.getId(),
                new VarianteProductoRequest("WPR-P", "Preservada", null, null, true, null, 0));
        ImagenProductoResponse imagen = productoService.createImagen(producto.getId(),
                new ImagenProductoRequest("https://example.test/p.jpg", null, false, 0));
        EspecificacionProductoResponse especificacion = productoService.createEspecificacion(producto.getId(),
                new EspecificacionProductoRequest("Clave", "Valor", null, 0));
        DocumentoProductoResponse documento = productoService.createDocumento(producto.getId(),
                new DocumentoProductoRequest("Catálogo", "https://example.test/c.pdf",
                        TipoDocumentoProducto.CATALOGO, "PDF", null));
        ConfiguracionCalculoResponse configuracion = productoService.upsertConfiguracionCalculo(producto.getId(),
                new ConfiguracionCalculoRequest(true, "Área", "m2", BigDecimal.ONE, "unidad", null));

        ProductoResponse actualizado = productoService.update(producto.getId(), new ProductoUpdateRequest(
                producto.getSku(), "Wall Panel Roble actualizado", producto.getSlug(), null, "Descripción nueva",
                null, null, null, EstadoDisponibilidad.DISPONIBLE, false, EstadoPublicacion.PUBLICADO,
                null, null, producto.getUnidadNegocio().getId(), Set.of()));

        assertThat(actualizado.variantes()).extracting(VarianteProductoResponse::id).contains(variante.id());
        assertThat(actualizado.imagenes()).extracting(ImagenProductoResponse::id).contains(imagen.id());
        assertThat(actualizado.especificaciones()).extracting(EspecificacionProductoResponse::id)
                .contains(especificacion.id());
        assertThat(actualizado.documentos()).extracting(DocumentoProductoResponse::id).contains(documento.id());
        assertThat(actualizado.configuracionCalculo().id()).isEqualTo(configuracion.id());
    }

    @Test
    void validaFlujoHttpCompletoEnWallPanelRobleSinDejarDatosTemporales() throws Exception {
        String base = "/api/productos/" + producto.getId();
        String especificacionJson = """
                {"clave":"Espesor","valor":"8 mm","grupo":"Dimensiones","orden":1}
                """;
        String especificacionCreada = mockMvc.perform(post(base + "/especificaciones")
                        .contentType(MediaType.APPLICATION_JSON).content(especificacionJson))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        String especificacionId = JsonPath.read(especificacionCreada, "$.id");

        mockMvc.perform(put(base + "/especificaciones/" + especificacionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"clave\":\"Espesor\",\"valor\":\"9 mm\",\"grupo\":\"Dimensiones\",\"orden\":2}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valor").value("9 mm"));
        mockMvc.perform(delete(base + "/especificaciones/" + especificacionId))
                .andExpect(status().isNoContent());

        String imagenCreada = mockMvc.perform(post(base + "/imagenes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"url\":\"https://example.test/wall-panel.jpg\",\"altText\":\"Wall Panel Roble\",\"esPrincipal\":true,\"orden\":1}"))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        String imagenId = JsonPath.read(imagenCreada, "$.id");

        mockMvc.perform(put(base + "/imagenes/" + imagenId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"url\":\"https://example.test/wall-panel-editado.jpg\",\"altText\":\"Detalle\",\"esPrincipal\":false,\"orden\":2}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.esPrincipal").value(false));
        mockMvc.perform(delete(base + "/imagenes/" + imagenId))
                .andExpect(status().isNoContent());

        String configuracion = """
                {"habilitada":true,"etiquetaEntrada":"Área","unidadEntrada":"m2","coberturaPorUnidad":2.4,"unidadVenta":"caja","textoAyuda":"Ayuda"}
                """;
        mockMvc.perform(put(base + "/configuracion-calculo")
                        .contentType(MediaType.APPLICATION_JSON).content(configuracion))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.habilitada").value(true));
        mockMvc.perform(put(base + "/configuracion-calculo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(configuracion.replace("\"habilitada\":true", "\"habilitada\":false")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.habilitada").value(false));

        mockMvc.perform(get(base))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.configuracionCalculo.habilitada").value(false));
        mockMvc.perform(get("/api/productos/publicados/slug/" + producto.getSlug()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.configuracionCalculo.coberturaPorUnidad").value(2.4));
    }

    private Producto crearProducto(String slug, EstadoPublicacion estado) {
        String sufijo = UUID.randomUUID().toString().substring(0, 8);
        Empresa empresa = empresaRepository.save(Empresa.builder()
                .razonSocial("ISANORTE " + sufijo)
                .nombreComercial("ISANORTE " + sufijo)
                .ruc(("20" + Math.abs(UUID.randomUUID().getLeastSignificantBits())).substring(0, 11))
                .build());
        UnidadNegocio unidad = unidadRepository.save(UnidadNegocio.builder()
                .nombre("ISADECO " + sufijo)
                .slug("isadeco-" + sufijo)
                .empresa(empresa)
                .build());
        Producto nuevo = Producto.builder()
                .sku("SKU-" + sufijo)
                .nombre("Wall Panel Roble")
                .slug(slug + "-" + sufijo)
                .descripcion("Panel decorativo de desarrollo")
                .disponibilidad(EstadoDisponibilidad.DISPONIBLE)
                .estado(estado)
                .build();
        unidad.addProducto(nuevo);
        return productoRepository.saveAndFlush(nuevo);
    }
}
