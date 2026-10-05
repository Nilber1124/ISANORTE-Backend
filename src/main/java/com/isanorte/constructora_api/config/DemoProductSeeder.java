package com.isanorte.constructora_api.config;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.isanorte.constructora_api.enums.EstadoDisponibilidad;
import com.isanorte.constructora_api.enums.EstadoPublicacion;
import com.isanorte.constructora_api.model.CategoriaProducto;
import com.isanorte.constructora_api.model.ConfiguracionCalculo;
import com.isanorte.constructora_api.model.EspecificacionProducto;
import com.isanorte.constructora_api.model.ImagenProducto;
import com.isanorte.constructora_api.model.Producto;
import com.isanorte.constructora_api.model.UnidadNegocio;
import com.isanorte.constructora_api.model.VarianteProducto;
import com.isanorte.constructora_api.repository.CategoriaProductoRepository;
import com.isanorte.constructora_api.repository.ProductoRepository;
import com.isanorte.constructora_api.repository.UnidadNegocioRepository;

import lombok.RequiredArgsConstructor;
import tools.jackson.databind.ObjectMapper;

/** Carga explícita e idempotente del catálogo de demostración de ISADECOR. */
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.seed.demo-products", havingValue = "true")
public class DemoProductSeeder implements ApplicationRunner {
    private static final Logger log = LoggerFactory.getLogger(DemoProductSeeder.class);
    private static final String DATA_RESOURCE = "classpath:data/isadecor-demo-products.json";

    private final ProductoRepository productoRepository;
    private final CategoriaProductoRepository categoriaRepository;
    private final UnidadNegocioRepository unidadRepository;
    private final ResourceLoader resourceLoader;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public void run(ApplicationArguments args) throws IOException {
        DemoCatalog catalog = readCatalog(resourceLoader.getResource(DATA_RESOURCE));
        UnidadNegocio unit = unidadRepository.findBySlug(catalog.businessUnitSlug())
                .orElseThrow(() -> new IllegalStateException(
                        "No existe la unidad de negocio requerida: " + catalog.businessUnitSlug()));

        Map<String, CategoriaProducto> activeCategories = new HashMap<>();
        categoriaRepository.findByActivoTrueOrderByOrdenAsc().stream()
                .filter(category -> category.getUnidadNegocio() == null
                        || unit.getId().equals(category.getUnidadNegocio().getId()))
                .forEach(category -> activeCategories.put(category.getSlug(), category));

        int inserted = 0;
        int skipped = 0;
        for (DemoProduct data : catalog.products()) {
            if (alreadySeeded(data)) {
                skipped++;
                continue;
            }
            Producto product = toProduct(data, unit, activeCategories, catalog.imageSets());
            productoRepository.save(product);
            inserted++;
        }
        log.info("Seed demo ISADECOR finalizado: {} productos insertados, {} omitidos por idempotencia.",
                inserted, skipped);
    }

    private DemoCatalog readCatalog(Resource resource) throws IOException {
        if (!resource.exists()) {
            throw new IllegalStateException("No se encontró el recurso de seed: " + DATA_RESOURCE);
        }
        return objectMapper.readValue(resource.getContentAsByteArray(), DemoCatalog.class);
    }

    private boolean alreadySeeded(DemoProduct data) {
        var bySku = productoRepository.findBySku(data.sku());
        var bySlug = productoRepository.findBySlug(data.slug());
        if (bySku.isEmpty() && bySlug.isEmpty()) {
            return false;
        }
        if (bySku.isPresent() && bySlug.isPresent() && bySku.get().getId().equals(bySlug.get().getId())) {
            return true;
        }
        throw new IllegalStateException("Conflicto de clave natural para producto demo " + data.sku()
                + ": SKU o slug pertenece a otro producto.");
    }

    private Producto toProduct(
            DemoProduct data, UnidadNegocio unit, Map<String, CategoriaProducto> activeCategories,
            Map<String, List<DemoImageTemplate>> imageSets) {
        Producto product = Producto.builder()
                .sku(data.sku())
                .nombre(data.name())
                .slug(data.slug())
                .resumen(data.summary())
                .descripcion(data.description())
                .precioBase(data.price())
                .precioAnterior(data.previousPrice())
                .descuentoPorcentaje(data.discountPercentage())
                .disponibilidad(data.availability())
                .destacado(data.featured())
                .retiroEnTienda(data.storePickup())
                .estado(EstadoPublicacion.PUBLICADO)
                .tituloSeo(data.seoTitle())
                .descripcionSeo(data.seoDescription())
                .build();
        unit.addProducto(product);

        for (String categorySlug : data.categorySlugs()) {
            CategoriaProducto category = activeCategories.get(categorySlug);
            if (category == null) {
                throw new IllegalStateException("La categoría activa '" + categorySlug
                        + "' no existe o no es compatible con ISADECOR. El seed no crea categorías.");
            }
            product.addCategoria(category);
        }
        List<DemoImageTemplate> images = imageSets.get(data.imageSet());
        if (images == null || images.isEmpty()) {
            throw new IllegalStateException("El set de imágenes demo no existe o está vacío: " + data.imageSet());
        }
        images.forEach(image -> product.addImagen(ImagenProducto.builder()
                .url(image.url()).altText(data.name() + " — " + image.label())
                .esPrincipal(image.primary()).orden(image.order()).build()));
        data.variants().forEach(variant -> product.addVariante(VarianteProducto.builder()
                .sku(variant.sku()).nombre(variant.name()).descripcion(variant.description())
                .precio(variant.price()).disponible(variant.available()).imagenUrl(variant.imageUrl())
                .orden(variant.order()).build()));
        addSpecification(product, "Material", data.material(), 0);
        addSpecification(product, "Acabado", data.finish(), 1);
        addSpecification(product, "Color", data.color(), 2);
        addSpecification(product, "Dimensiones", data.dimensions(), 3);
        addSpecification(product, "Espesor", data.thickness(), 4);
        addSpecification(product, "Uso recomendado", data.recommendedUse(), 5);
        addSpecification(product, "Resistencia a humedad", data.moistureResistance(), 6);
        addSpecification(product, "Instalación", data.installation(), 7);
        if (data.calculation() != null) {
            DemoCalculation calculation = data.calculation();
            product.setConfiguracionCalculo(ConfiguracionCalculo.builder()
                    .habilitada(true)
                    .etiquetaEntrada(calculation.inputLabel())
                    .unidadEntrada(calculation.inputUnit())
                    .coberturaPorUnidad(calculation.coveragePerUnit())
                    .unidadVenta(calculation.salesUnit())
                    .textoAyuda(calculation.helpText())
                    .build());
        }
        return product;
    }

    private void addSpecification(Producto product, String key, String value, int order) {
        product.addEspecificacion(EspecificacionProducto.builder()
                .clave(key).valor(value).grupo("Ficha técnica").orden(order).build());
    }

    record DemoCatalog(
            String businessUnitSlug, Map<String, List<DemoImageTemplate>> imageSets,
            List<DemoProduct> products) { }

    record DemoProduct(
            String sku, String name, String slug, String summary, String description,
            BigDecimal price, BigDecimal previousPrice, BigDecimal discountPercentage,
            EstadoDisponibilidad availability, boolean featured, boolean storePickup,
            String seoTitle, String seoDescription, List<String> categorySlugs,
            String imageSet, String material, String finish, String color, String dimensions,
            String thickness, String recommendedUse, String moistureResistance, String installation,
            List<DemoVariant> variants, DemoCalculation calculation) { }

    record DemoImageTemplate(String url, String label, boolean primary, int order) { }

    record DemoVariant(
            String sku, String name, String description, BigDecimal price,
            boolean available, String imageUrl, int order) { }

    record DemoCalculation(
            String inputLabel, String inputUnit, BigDecimal coveragePerUnit,
            String salesUnit, String helpText) { }
}
