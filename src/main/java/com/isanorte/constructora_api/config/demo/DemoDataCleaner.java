package com.isanorte.constructora_api.config.demo;

import static com.isanorte.constructora_api.config.demo.DemoSupport.EMAIL_DOMAIN;
import static com.isanorte.constructora_api.config.demo.DemoSupport.id;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

/**
 * Elimina únicamente lo que sembró la carga demo ({@code SEED_DEMO_DATA_CLEAN=true}).
 *
 * <p>Se reconoce por: correos {@code @demo.isanorte.ec}, códigos {@code COT-/PED-…-DEMOnnnn}, los SKU del
 * catálogo demo y los ids deterministas de las filas nuevas. No revierte las imágenes que la carga
 * reemplazó en filas de la migración inicial (hero, servicios, proyectos…): esas quedan en Cloudinary.
 * Debe ejecutarse con {@code SEED_DEMO_DATA} desactivada.
 */
@Component
@RequiredArgsConstructor
@Order(5)
@ConditionalOnProperty(name = "app.seed.demo-data-clean", havingValue = "true")
public class DemoDataCleaner implements ApplicationRunner {
    private static final Logger log = LoggerFactory.getLogger(DemoDataCleaner.class);

    private final DemoSupport support;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public void run(ApplicationArguments args) throws IOException {
        JdbcTemplate jdbc = support.jdbc();
        String demoClients = "(select id from clientes where email like '%" + EMAIL_DOMAIN + "')";
        String demoOrders = "(select id from pedidos where codigo like 'PED-%-DEMO____')";
        String demoQuotes = "(select id from cotizaciones where codigo like 'COT-%-DEMO____')";

        jdbc.update("delete from resenas_producto where cliente_id in " + demoClients);
        jdbc.update("delete from seguimientos_pedido where pedido_id in " + demoOrders);
        jdbc.update("delete from detalles_pedido where pedido_id in " + demoOrders);
        jdbc.update("delete from pedidos where codigo like 'PED-%-DEMO____'");
        jdbc.update("delete from seguimientos_cotizacion where cotizacion_id in " + demoQuotes);
        jdbc.update("delete from detalles_cotizacion where cotizacion_id in " + demoQuotes);
        jdbc.update("delete from cotizaciones where codigo like 'COT-%-DEMO____'");
        jdbc.update("delete from carritos_cliente where cliente_id in " + demoClients);
        jdbc.update("delete from direcciones_cliente where cliente_id in " + demoClients);
        jdbc.update("delete from clientes where email like '%" + EMAIL_DOMAIN + "'");
        jdbc.update("delete from solicitudes_contacto where email like '%" + EMAIL_DOMAIN + "'");

        for (String sku : demoSkus()) {
            UUID producto = support.findId("select id from productos where sku = ?", sku);
            if (producto == null) continue;
            for (String child : List.of("documentos_producto", "categoria_producto", "variantes_producto", "imagenes_producto",
                    "especificaciones_producto", "configuraciones_calculo", "resenas_producto")) {
                jdbc.update("delete from " + child + " where producto_id = ?", producto);
            }
            jdbc.update("delete from productos where id = ?", producto);
        }

        deleteByIds(jdbc, "anuncios", List.of("wall-panels", "pisos-spc", "revestimientos", "cielos-rasos", "asesoria", "obra-nueva",
                "instalacion", "dormitorio", "oficinas", "exteriores", "vencido", "programado"));
        deleteByIds(jdbc, "catalogos", List.of("catalogo-wall-panels", "catalogo-pisos", "catalogo-revestimientos"));
        deleteByIds(jdbc, "categorias_producto", List.of("wall-panels", "revestimientos", "pisos", "paneles-listonados",
                "paneles-acusticos", "paneles-decorativos"));
        deleteByIds(jdbc, "redes_sociales", List.of("Facebook", "Instagram", "LinkedIn", "YouTube"));
        for (String slug : List.of("diseno-arquitectonico", "remodelacion")) {
            UUID servicio = id("servicios", slug);
            jdbc.update("delete from proyecto_servicio where servicio_id = ?", servicio);
            jdbc.update("delete from beneficios_servicio where servicio_id = ?", servicio);
            jdbc.update("delete from servicios where id = ?", servicio);
        }
        for (String slug : List.of("conjunto-los-pinos", "restaurante-raiz", "hotel-boutique-andino", "centro-comercial-plaza-norte")) {
            UUID proyecto = id("proyectos", slug);
            jdbc.update("delete from proyecto_servicio where proyecto_id = ?", proyecto);
            jdbc.update("delete from proyectos where id = ?", proyecto);
        }
        for (String permiso : List.of("PRODUCTOS_LEER", "PRODUCTOS_EDITAR", "COTIZACIONES_LEER", "COTIZACIONES_GESTIONAR",
                "PEDIDOS_LEER", "PEDIDOS_GESTIONAR", "ANUNCIOS_EDITAR", "CONTENIDO_EDITAR")) {
            jdbc.update("delete from rol_permiso where permiso_id = ?", id("permisos", permiso));
            jdbc.update("delete from permisos where id = ?", id("permisos", permiso));
        }
        for (String rol : List.of("VENTAS", "CONTENIDO")) {
            jdbc.update("delete from administrador_rol where rol_id = ?", id("roles", rol));
            jdbc.update("delete from roles where id = ?", id("roles", rol));
        }
        log.info("Datos demo eliminados. Recuerda desactivar SEED_DEMO_DATA_CLEAN.");
    }

    private void deleteByIds(JdbcTemplate jdbc, String table, List<String> keys) {
        for (String key : keys) {
            jdbc.update("delete from " + table + " where id = ?", id(table, key));
        }
    }

    private List<String> demoSkus() throws IOException {
        List<String> skus = new ArrayList<>();
        try (InputStream in = new ClassPathResource("data/isadecor-demo-products.json").getInputStream()) {
            JsonNode root = objectMapper.readTree(in);
            root.get("products").forEach(product -> skus.add(product.get("sku").asString()));
        }
        return skus;
    }
}
