package com.isanorte.constructora_api;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

class InitialIsanorteContentMigrationTest {

    private static String migration;

    @BeforeAll
    static void readMigration() throws IOException {
        migration = new ClassPathResource("db/migration/V4__contenido_inicial_isanorte.sql")
                .getContentAsString(StandardCharsets.UTF_8);
    }

    @Test
    void exigeUnaEmpresaYUnaConfiguracionAntesDeMigrar() {
        assertThat(migration)
                .contains("v_empresa_count <> 1")
                .contains("v_sitio_count <> 1")
                .contains("clave = 'isanorte'");
    }

    @Test
    void defineLasCincoSeccionesDeHomeEnOrden() {
        assertThat(migration)
                .contains("'HERO'")
                .contains("'SERVICIOS'")
                .contains("'UNIDAD_NEGOCIO'")
                .contains("'PROYECTOS'")
                .contains("'CTA'")
                .contains("'#cotizar'")
                .contains("'#contacto'");
        assertThat(migration.indexOf("'HERO'"))
                .isLessThan(migration.indexOf("'SERVICIOS'"))
                .isLessThan(migration.indexOf("'UNIDAD_NEGOCIO'"))
                .isLessThan(migration.indexOf("'PROYECTOS'"))
                .isLessThan(migration.indexOf("'CTA'"));
    }

    @Test
    void incluyeEscenasServiciosBeneficiosYUnidadDestacada() {
        assertThat(migration)
                .contains("/images/recorrido-exterior.jpg")
                .contains("/images/recorrido-dormitorio.jpg")
                .contains("'construccion'")
                .contains("'acabados'")
                .contains("'decoracion'")
                .contains("'Estudios de suelo & cimentación'")
                .contains("'Planos de distribución 3D fotorealistas'")
                .contains("'isadecor'")
                .contains("TRUE, TRUE");
    }

    @Test
    void conservaTagsYEstadisticasComoDatosEstructurados() {
        assertThat(migration)
                .contains("(0, 'ARQUITECTURA')")
                .contains("(1, 'CONSTRUCCIÓN')")
                .contains("(2, 'OBRA CIVIL')")
                .contains("(3, 'ACABADOS')")
                .contains("120.00, '+', NULL::VARCHAR, 'PROYECTOS ENTREGADOS'")
                .contains("98.00, NULL::VARCHAR, '%', 'CLIENTES SATISFECHOS'");
    }

    @Test
    void relacionaSoloProyectosConServiciosInferibles() {
        assertThat(migration)
                .contains("('villa-lomas-alta', 'construccion')")
                .contains("('penthouse-abisal', 'decoracion')")
                .contains("('clinica-sanitas-norte', 'construccion')")
                .contains("('showroom-isadecor', 'decoracion')")
                .doesNotContain("('oficinas-nexus', 'corporativo')");
    }

    @Test
    void noCreaCatalogoSolicitudesDemoNiTrustStrip() {
        String lower = migration.toLowerCase();
        assertThat(lower)
                .doesNotContain("insert into solicitudes_contacto")
                .doesNotContain("'catalogo',")
                .doesNotContain("google")
                .doesNotContain("amazon")
                .doesNotContain("netflix")
                .doesNotContain("microsoft")
                .doesNotContain("delete from")
                .doesNotContain("truncate")
                .doesNotContain("drop table");
    }
}
