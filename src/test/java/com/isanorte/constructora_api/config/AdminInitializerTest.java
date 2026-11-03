package com.isanorte.constructora_api.config;

import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.ApplicationArguments;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.isanorte.constructora_api.model.Administrador;
import com.isanorte.constructora_api.model.Rol;
import com.isanorte.constructora_api.repository.AdministradorRepository;
import com.isanorte.constructora_api.repository.RolRepository;

@ExtendWith(MockitoExtension.class)
class AdminInitializerTest {
    @Mock private AdministradorRepository administradorRepository;
    @Mock private RolRepository rolRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private ApplicationArguments arguments;

    @Test
    void createsConfiguredAdministrator() {
        var properties = new AuthProperties("administrador", "admin@example.com", "password-safe-123");
        when(administradorRepository.findByEmailIgnoreCase("admin@example.com")).thenReturn(Optional.empty());
        when(rolRepository.findByNombreIgnoreCase("ADMINISTRADOR")).thenReturn(Optional.of(
                Rol.builder().nombre("ADMINISTRADOR").activo(true).build()));
        when(passwordEncoder.encode("password-safe-123")).thenReturn("encoded");

        new AdminInitializer(administradorRepository, rolRepository, passwordEncoder, properties).run(arguments);

        verify(administradorRepository).save(any(Administrador.class));
    }

    @Test
    void skipsWhenBothCredentialsAreBlank() {
        var properties = new AuthProperties("ADMINISTRADOR", " ", null);

        new AdminInitializer(administradorRepository, rolRepository, passwordEncoder, properties).run(arguments);

        verifyNoInteractions(administradorRepository, rolRepository, passwordEncoder);
    }

    @Test
    void rejectsAdversarialEmailInBoundedTimeWithoutSaving() {
        String email = "a".repeat(300_000) + "@example";
        var properties = new AuthProperties("ADMINISTRADOR", email, "password-safe-123");
        when(administradorRepository.findByEmailIgnoreCase(email)).thenReturn(Optional.empty());
        var initializer = new AdminInitializer(administradorRepository, rolRepository, passwordEncoder, properties);

        assertTimeoutPreemptively(Duration.ofSeconds(1),
                () -> assertThrows(IllegalStateException.class, () -> initializer.run(arguments)));
        verify(administradorRepository, never()).save(any());
        verifyNoInteractions(rolRepository, passwordEncoder);
    }
}
