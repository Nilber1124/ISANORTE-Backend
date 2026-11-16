package com.isanorte.constructora_api.config.demo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

import tools.jackson.databind.ObjectMapper;

class DemoManifestTest {
    @Test
    void devuelveUrlsDeCloudinaryDelManifiesto() {
        DemoManifest manifest = DemoManifest.load(new ObjectMapper());

        assertThat(manifest.has("hero-exterior")).isTrue();
        assertThat(manifest.url("hero-exterior")).contains("res.cloudinary.com");
    }

    @Test
    void fallaConClaveInexistente() {
        DemoManifest manifest = DemoManifest.load(new ObjectMapper());

        assertThatThrownBy(() -> manifest.url("no-existe"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("no-existe");
    }
}
