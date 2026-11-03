package com.isanorte.constructora_api.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.util.unit.DataSize;

class MultipartConfigurationTest {

    private static final Pattern DEFAULT_VALUE = Pattern.compile("^\\$\\{[^:}]+:([^}]+)}$");
    private static final DataSize SONAR_RECOMMENDED_MAXIMUM = DataSize.ofMegabytes(8);

    @ParameterizedTest
    @ValueSource(strings = { "application.example.properties", "application-render.properties" })
    void defineLimitesMultipartSegurosYConfigurables(String resourceName) throws IOException {
        Properties properties = load(resourceName);

        String fileProperty = properties.getProperty("spring.servlet.multipart.max-file-size");
        String requestProperty = properties.getProperty("spring.servlet.multipart.max-request-size");
        DataSize fileLimit = DataSize.parse(defaultValue(fileProperty));
        DataSize requestLimit = DataSize.parse(defaultValue(requestProperty));

        assertEquals("${MULTIPART_MAX_FILE_SIZE:7MB}", fileProperty);
        assertEquals("${MULTIPART_MAX_REQUEST_SIZE:8MB}", requestProperty);
        assertTrue(fileLimit.compareTo(requestLimit) < 0,
                "La petición debe dejar margen para la envoltura multipart");
        assertTrue(requestLimit.compareTo(SONAR_RECOMMENDED_MAXIMUM) <= 0,
                "El valor seguro por defecto no debe superar 8 MB");
    }

    private static Properties load(String resourceName) throws IOException {
        Properties properties = new Properties();
        try (InputStream input = MultipartConfigurationTest.class.getClassLoader().getResourceAsStream(resourceName)) {
            if (input == null) {
                throw new IOException("No se encontró el recurso " + resourceName);
            }
            properties.load(input);
        }
        return properties;
    }

    private static String defaultValue(String placeholder) {
        Matcher matcher = DEFAULT_VALUE.matcher(placeholder == null ? "" : placeholder);
        assertTrue(matcher.matches(), "La propiedad debe usar una variable con valor seguro por defecto");
        return matcher.group(1);
    }
}
