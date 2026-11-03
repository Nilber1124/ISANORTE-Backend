package com.isanorte.constructora_api.service.implementation;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Method;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

class TransactionalSelfInvocationAssessmentTest {
    @Test
    void flaggedEntryPointsAlreadyDefineEquivalentProxyTransactionBoundaries() {
        Map<Class<?>, Map<String, Boolean>> methods = Map.of(
                CategoriaProductoService.class, Map.of("createResponse", false),
                ConfiguracionSitioService.class, Map.of("createResponse", false),
                CotizacionService.class, Map.of("createResponse", false, "createPublic", false),
                EmpresaService.class, Map.of("createResponse", false),
                ProductoService.class, Map.of("createResponse", false),
                ProyectoService.class, Map.of("findByIdResponse", true, "createResponse", false),
                SeccionLandingService.class, Map.of("createResponse", false),
                ServicioService.class, Map.of("createResponse", false),
                UnidadNegocioService.class, Map.of("createResponse", false));

        methods.forEach((type, expectations) -> expectations.forEach((name, readOnly) -> {
            Method method = findUniqueMethod(type, name);
            Transactional transaction = method.getAnnotation(Transactional.class);
            assertThat(transaction).as(type.getSimpleName() + "." + name).isNotNull();
            assertThat(transaction.propagation()).isEqualTo(Propagation.REQUIRED);
            assertThat(transaction.readOnly()).isEqualTo(readOnly);
        }));
    }

    private static Method findUniqueMethod(Class<?> type, String name) {
        Method[] matches = java.util.Arrays.stream(type.getDeclaredMethods())
                .filter(method -> method.getName().equals(name))
                .toArray(Method[]::new);
        assertThat(matches).as(type.getSimpleName() + "." + name).hasSize(1);
        return matches[0];
    }
}
