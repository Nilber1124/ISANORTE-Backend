package com.isanorte.constructora_api.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.util.Set;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.isanorte.constructora_api.dto.request.ConfiguracionSitioRequest;
import com.isanorte.constructora_api.dto.request.ConfiguracionSitioUpdateRequest;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

class ConfiguracionSitioRegexTest {
    private static ValidatorFactory factory;
    private static Validator validator;

    @BeforeAll
    static void createValidator() {
        factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @AfterAll
    static void closeValidator() {
        factory.close();
    }

    @Test
    void acceptsCanonicalKeysAndRejectsPartialOrRepeatedSeparators() {
        assertTrue(validateCreate("sitio-norte-2026").isEmpty());
        assertTrue(validator.validate(new ConfiguracionSitioUpdateRequest(
                "isadecor", null, null, null, null, null, null, null, null)).isEmpty());
        assertFalse(validateCreate("-sitio").isEmpty());
        assertFalse(validateCreate("sitio--norte").isEmpty());
        assertFalse(validateCreate("sitio_norte").isEmpty());
    }

    @Test
    void rejectsVeryLargeKeyWithoutRegexStackOverflow() {
        String adversarial = "a-".repeat(250_000) + "!";
        assertTimeoutPreemptively(Duration.ofSeconds(1), () -> {
            assertFalse(validateCreate(adversarial).isEmpty());
            assertFalse(validator.validate(new ConfiguracionSitioUpdateRequest(
                    adversarial, null, null, null, null, null, null, null, null)).isEmpty());
        });
    }

    @Test
    void normalizesUnicodeAndStripsOnlyEdgeHyphens() {
        ConfiguracionSitio site = ConfiguracionSitio.builder().clave("  --ÁREA NORTE--  ").build();
        site.onCreate();
        assertEquals("area-norte", site.getClave());
    }

    @Test
    void normalizationHandlesLongSeparatorRunsInBoundedTime() {
        ConfiguracionSitio site = ConfiguracionSitio.builder()
                .clave("-".repeat(300_000) + "sitio" + "-".repeat(300_000)).build();
        assertTimeoutPreemptively(Duration.ofSeconds(1), site::onCreate);
        assertEquals("sitio", site.getClave());
    }

    private static Set<ConstraintViolation<ConfiguracionSitioRequest>> validateCreate(String key) {
        return validator.validate(new ConfiguracionSitioRequest(
                java.util.UUID.randomUUID(), key, null, null, null, null, null,
                null, null, null, null));
    }
}
