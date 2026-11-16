package com.isanorte.constructora_api.config.demo;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import com.isanorte.constructora_api.config.DemoProductSeeder;
import com.isanorte.constructora_api.repository.CategoriaProductoRepository;
import com.isanorte.constructora_api.repository.ProductoRepository;
import com.isanorte.constructora_api.repository.UnidadNegocioRepository;

import tools.jackson.databind.ObjectMapper;

/** Ejecuta las tres fases del seed demo contra H2 y comprueba reglas de negocio e idempotencia. */
@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(properties = "app.auth.admin-role=ADMINISTRADOR")
@Transactional
class DemoDataSeedersIntegrationTest {
    @Autowired private EntityManager entityManager;
    @Autowired private JdbcTemplate jdbc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private ProductoRepository productoRepository;
    @Autowired private CategoriaProductoRepository categoriaRepository;
    @Autowired private UnidadNegocioRepository unidadRepository;

    private void runAll() throws Exception {
        DemoSupport support = new DemoSupport(jdbc, objectMapper);
        new DemoContentSeeder(support).run(null);
        new DemoProductSeeder(productoRepository, categoriaRepository, unidadRepository,
                new DefaultResourceLoader(), support, objectMapper).run(null);
        entityManager.flush(); // en producción cada fase confirma su propia transacción
        new DemoCustomerSeeder(support, passwordEncoder, objectMapper).run(null);
    }

    private void baseRows() {
        UUID empresa = UUID.randomUUID();
        jdbc.update("insert into empresa (id, razon_social, nombre_comercial, ruc, fecha_creacion, fecha_actualizacion) values (?, 'Test S.A.', 'Test', '1790000000001', now(), now())", empresa);
        jdbc.update("insert into configuraciones_sitio (id, clave, empresa_id, fecha_actualizacion) values (?, 'isanorte', ?, now())", UUID.randomUUID(), empresa);
        jdbc.update("insert into unidades_negocio (id, nombre, slug, activo, destacado, orden, empresa_id, fecha_creacion, fecha_actualizacion) values (?, 'ISADECOR', 'isadecor', true, true, 0, ?, now(), now())", UUID.randomUUID(), empresa);
    }

    private int count(String sql) {
        return jdbc.queryForObject(sql, Integer.class);
    }

    @Test
    void sembrarDosVecesDejaDatosCoherentesYSinDuplicados() throws Exception {
        baseRows();
        runAll();

        int pedidos = count("select count(*) from pedidos");
        int cotizaciones = count("select count(*) from cotizaciones");
        int resenas = count("select count(*) from resenas_producto");
        int productos = count("select count(*) from productos");
        assertThat(productos).isEqualTo(26);
        assertThat(pedidos).isEqualTo(15);
        assertThat(cotizaciones).isEqualTo(15);
        assertThat(resenas).isEqualTo(40);
        assertThat(count("select count(*) from anuncios")).isEqualTo(12);
        assertThat(count("select count(*) from clientes")).isEqualTo(12);
        assertThat(count("select count(*) from catalogos")).isEqualTo(3);

        // Totales = suma de líneas; sin IVA.
        assertThat(count("select count(*) from pedidos p where total_pedido is distinct from (select sum(subtotal) from detalles_pedido d where d.pedido_id = p.id)")).isZero();
        assertThat(count("select count(*) from cotizaciones c where total_estimado is distinct from (select sum(subtotal) from detalles_cotizacion d where d.cotizacion_id = c.id)")).isZero();
        // Stock: cancelado no descuenta; nunca negativo.
        assertThat(count("select count(*) from pedidos where (estado = 'CANCELADO') = stock_descontado")).isZero();
        assertThat(count("select count(*) from productos where stock_actual < 0")).isZero();
        // Todas las imágenes salen de Cloudinary.
        assertThat(count("select count(*) from imagenes_producto where url not like 'https://res.cloudinary.com/%'")).isZero();
        assertThat(count("select count(*) from anuncios where imagen_url not like 'https://res.cloudinary.com/%'")).isZero();

        runAll();

        assertThat(count("select count(*) from pedidos")).isEqualTo(pedidos);
        assertThat(count("select count(*) from cotizaciones")).isEqualTo(cotizaciones);
        assertThat(count("select count(*) from resenas_producto")).isEqualTo(resenas);
        assertThat(count("select count(*) from productos")).isEqualTo(productos);
    }

    @Test
    void laLimpiezaEliminaSoloLoSembrado() throws Exception {
        baseRows();
        runAll();

        new DemoDataCleaner(new DemoSupport(jdbc, objectMapper), objectMapper).run(null);

        for (String table : new String[] {"pedidos", "cotizaciones", "clientes", "resenas_producto", "productos", "anuncios",
                "catalogos", "categorias_producto", "solicitudes_contacto", "direcciones_cliente", "carritos_cliente"}) {
            assertThat(count("select count(*) from " + table)).as(table).isZero();
        }
        // Lo que no es demo (empresa, sitio, unidad) permanece.
        assertThat(count("select count(*) from unidades_negocio where slug = 'isadecor'")).isEqualTo(1);
    }
}
