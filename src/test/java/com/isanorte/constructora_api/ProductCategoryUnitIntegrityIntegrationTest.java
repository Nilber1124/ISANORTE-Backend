package com.isanorte.constructora_api;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.test.web.servlet.MockMvc;

import com.isanorte.constructora_api.dto.request.CategoriaProductoRequest;
import com.isanorte.constructora_api.dto.request.CategoriaProductoUpdateRequest;
import com.isanorte.constructora_api.dto.request.ProductoRequest;
import com.isanorte.constructora_api.dto.request.ProductoUpdateRequest;
import com.isanorte.constructora_api.dto.response.CategoriaProductoResponse;
import com.isanorte.constructora_api.enums.EstadoDisponibilidad;
import com.isanorte.constructora_api.enums.EstadoPublicacion;
import com.isanorte.constructora_api.model.Empresa;
import com.isanorte.constructora_api.model.Producto;
import com.isanorte.constructora_api.model.UnidadNegocio;
import com.isanorte.constructora_api.repository.EmpresaRepository;
import com.isanorte.constructora_api.repository.ProductoRepository;
import com.isanorte.constructora_api.repository.UnidadNegocioRepository;
import com.isanorte.constructora_api.service.ICategoriaProductoService;
import com.isanorte.constructora_api.service.IProductoService;

import tools.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class ProductCategoryUnitIntegrityIntegrationTest {

    @Autowired private IProductoService productoService;
    @Autowired private ICategoriaProductoService categoriaService;
    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private ProductoRepository productoRepository;
    @Autowired private EmpresaRepository empresaRepository;
    @Autowired private UnidadNegocioRepository unidadRepository;

    @Test
    void creationAndProductUpdatesOnlyAcceptGlobalOrSameUnitCategories() {
        UnidadNegocio unitA = unit("a");
        UnidadNegocio unitB = unit("b");
        CategoriaProductoResponse global = category("global", null);
        CategoriaProductoResponse scopedA = category("scoped-a", unitA.getId());
        CategoriaProductoResponse scopedB = category("scoped-b", unitB.getId());

        Producto withGlobal = productoService.create(productRequest("global", unitA.getId(), Set.of(global.id())));
        Producto withScoped = productoService.create(productRequest("scoped", unitA.getId(), Set.of(scopedA.id())));
        Producto withoutCategory = productoService.create(productRequest("without", unitA.getId(), null));
        assertEquals(1, withGlobal.getCategorias().size());
        assertEquals(1, withScoped.getCategorias().size());
        assertEquals(0, withoutCategory.getCategorias().size());

        IllegalStateException invalidCreate = assertThrows(IllegalStateException.class,
                () -> productoService.create(productRequest("invalid", unitA.getId(), Set.of(scopedB.id()))));
        assertEquals("La categoría seleccionada no pertenece a la unidad de negocio del producto.",
                invalidCreate.getMessage());

        assertDoesNotThrow(() -> productoService.update(withScoped.getId(),
                productUpdate(withScoped, unitA.getId(), Set.of(global.id()))));
        IllegalStateException invalidCategoryChange = assertThrows(IllegalStateException.class,
                () -> productoService.update(withScoped.getId(),
                        productUpdate(withScoped, unitA.getId(), Set.of(scopedB.id()))));
        assertEquals("La categoría seleccionada no pertenece a la unidad de negocio del producto.",
                invalidCategoryChange.getMessage());

        assertDoesNotThrow(() -> productoService.update(withGlobal.getId(),
                productUpdate(withGlobal, unitB.getId(), Set.of(global.id()))));
        Producto movedWithGlobal = productoRepository.findById(withGlobal.getId()).orElseThrow();
        assertEquals(unitB.getId(), movedWithGlobal.getUnidadNegocio().getId());

        IllegalStateException invalidUnitChange = assertThrows(IllegalStateException.class,
                () -> productoService.update(withScoped.getId(),
                        productUpdate(withScoped, unitB.getId(), Set.of(scopedA.id()))));
        assertEquals("La categoría seleccionada no pertenece a la unidad de negocio del producto.",
                invalidUnitChange.getMessage());
    }

    @Test
    void categoryScopeChangesRejectOnlyWhenTheyWouldBreakAssociatedProducts() {
        UnidadNegocio unitA = unit("scope-a");
        UnidadNegocio unitB = unit("scope-b");
        CategoriaProductoResponse scopedA = category("scoped-to-global", unitA.getId());
        Producto scopedProduct = productoService.create(productRequest(
                "scoped-product", unitA.getId(), Set.of(scopedA.id())));

        CategoriaProductoResponse globalized = categoriaService.update(scopedA.id(), categoryUpdate(scopedA, null));
        assertNull(globalized.unidadNegocio());

        CategoriaProductoResponse unusedGlobal = category("unused-global", null);
        CategoriaProductoResponse newlyScoped = categoriaService.update(
                unusedGlobal.id(), categoryUpdate(unusedGlobal, unitB.getId()));
        assertEquals(unitB.getId(), newlyScoped.unidadNegocio().id());

        CategoriaProductoResponse globalUsedOnlyByA = category("global-a", null);
        productoService.create(productRequest("global-a-product", unitA.getId(), Set.of(globalUsedOnlyByA.id())));
        assertEquals(unitA.getId(), categoriaService.update(
                globalUsedOnlyByA.id(), categoryUpdate(globalUsedOnlyByA, unitA.getId())).unidadNegocio().id());

        CategoriaProductoResponse globalUsedByBoth = category("global-both", null);
        productoService.create(productRequest("global-a-use", unitA.getId(), Set.of(globalUsedByBoth.id())));
        productoService.create(productRequest("global-b-use", unitB.getId(), Set.of(globalUsedByBoth.id())));
        assertScopeConflict(() -> categoriaService.update(
                globalUsedByBoth.id(), categoryUpdate(globalUsedByBoth, unitA.getId())));

        CategoriaProductoResponse scopedStillA = category("scoped-a-to-b", unitA.getId());
        productoService.create(productRequest("scoped-a-use", unitA.getId(), Set.of(scopedStillA.id())));
        assertScopeConflict(() -> categoriaService.update(
                scopedStillA.id(), categoryUpdate(scopedStillA, unitB.getId())));

        // The product remains associated after scoped-to-global conversion; only its scope expanded.
        assertEquals(1, productoRepository.findById(scopedProduct.getId()).orElseThrow().getCategorias().size());
    }

    @Test
    void directAdminRequestWithACrossUnitCategoryReturnsConflict() throws Exception {
        UnidadNegocio unitA = unit("http-a");
        UnidadNegocio unitB = unit("http-b");
        CategoriaProductoResponse categoryB = category("http-b", unitB.getId());

        Map<String, Object> request = Map.of(
                "sku", "SKU-HTTP-" + random(5), "nombre", "Producto HTTP", "slug", "product-http-" + random(5),
                "descripcion", "Descripción", "disponibilidad", "DISPONIBLE", "destacado", false,
                "estado", "BORRADOR", "unidadNegocioId", unitA.getId(), "categoriaIds", List.of(categoryB.id()));
        mockMvc.perform(post("/api/productos").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message").value(
                        "La categoría seleccionada no pertenece a la unidad de negocio del producto."));
    }

    private void assertScopeConflict(Runnable operation) {
        IllegalStateException exception = assertThrows(IllegalStateException.class, operation::run);
        assertEquals("No se puede cambiar la unidad de la categoría porque dejaría productos incompatibles.",
                exception.getMessage());
    }

    private CategoriaProductoResponse category(String seed, UUID unitId) {
        return categoriaService.createResponse(new CategoriaProductoRequest(
                "Categoría " + seed + random(4), "category-" + seed + "-" + random(5), null, null,
                true, 0, unitId));
    }

    private ProductoRequest productRequest(String seed, UUID unitId, Set<UUID> categoryIds) {
        return new ProductoRequest(
                "SKU-" + seed + "-" + random(5), "Producto " + seed + random(4),
                "product-" + seed + "-" + random(5), null, "Descripción", null, null, null,
                EstadoDisponibilidad.DISPONIBLE, false, EstadoPublicacion.BORRADOR, null, null,
                unitId, categoryIds, null, null, null, null, null);
    }

    private ProductoUpdateRequest productUpdate(Producto product, UUID unitId, Set<UUID> categoryIds) {
        return new ProductoUpdateRequest(
                product.getSku(), product.getNombre(), product.getSlug(), product.getResumen(), product.getDescripcion(),
                product.getPrecioBase(), product.getPrecioAnterior(), product.getDescuentoPorcentaje(),
                product.getDisponibilidad(), Boolean.TRUE.equals(product.getDestacado()), product.getEstado(),
                product.getTituloSeo(), product.getDescripcionSeo(), unitId, categoryIds);
    }

    private CategoriaProductoUpdateRequest categoryUpdate(CategoriaProductoResponse category, UUID unitId) {
        return new CategoriaProductoUpdateRequest(
                category.nombre(), category.slug(), category.descripcion(), category.imagenUrl(),
                Boolean.TRUE.equals(category.activo()), category.orden(), unitId);
    }

    private UnidadNegocio unit(String seed) {
        Empresa company = empresaRepository.saveAndFlush(Empresa.builder().razonSocial("Razón " + seed + random(5))
                .nombreComercial("Comercial " + seed).ruc("R" + random(19)).build());
        return unidadRepository.saveAndFlush(UnidadNegocio.builder().nombre("Unidad " + seed + random(5))
                .slug("unit-" + seed + "-" + random(5)).empresa(company).activo(true).orden(0).build());
    }

    private String random(int length) {
        return UUID.randomUUID().toString().replace("-", "").substring(0, length);
    }
}
