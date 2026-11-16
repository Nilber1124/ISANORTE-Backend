package com.isanorte.constructora_api.config.demo;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.core.io.ClassPathResource;

import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

/**
 * Manifiesto clave → URL de Cloudinary de las imágenes y PDF de demostración.
 *
 * <p>Lo escribe el script de subida (fuera del repositorio) y solo contiene texto: ninguna imagen
 * viaja con la aplicación. Si falta una clave o la URL no es de Cloudinary, la carga se detiene con un
 * mensaje claro en lugar de guardar rutas locales.
 */
public final class DemoManifest {
    static final String RESOURCE = "data/demo-manifest.json";

    private final Map<String, String> urls;

    private DemoManifest(Map<String, String> urls) {
        this.urls = urls;
    }

    public static DemoManifest load(ObjectMapper objectMapper) {
        ClassPathResource resource = new ClassPathResource(RESOURCE);
        if (!resource.exists()) {
            throw new IllegalStateException("Falta " + RESOURCE
                    + ". Genera las imágenes y súbelas a Cloudinary con demo-cloudinary/subir.py antes de sembrar.");
        }
        try (InputStream in = resource.getInputStream()) {
            Map<String, String> data = objectMapper.readValue(in, new TypeReference<TreeMap<String, String>>() {
            });
            return new DemoManifest(data);
        } catch (IOException ex) {
            throw new IllegalStateException("No se pudo leer " + RESOURCE, ex);
        }
    }

    /** URL de Cloudinary de la clave indicada. */
    public String url(String key) {
        String url = urls.get(key);
        if (url == null || !url.contains("res.cloudinary.com")) {
            throw new IllegalStateException("El manifiesto no tiene una URL de Cloudinary para la clave: " + key);
        }
        return url;
    }

    public boolean has(String key) {
        return urls.containsKey(key);
    }
}
