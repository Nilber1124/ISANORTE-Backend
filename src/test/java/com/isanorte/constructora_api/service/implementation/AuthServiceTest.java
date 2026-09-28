package com.isanorte.constructora_api.service.implementation;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.isanorte.constructora_api.config.AuthProperties;
import com.isanorte.constructora_api.dto.request.LoginRequest;
import com.isanorte.constructora_api.exception.CredencialesInvalidasException;
import com.isanorte.constructora_api.model.Administrador;
import com.isanorte.constructora_api.model.Rol;
import com.isanorte.constructora_api.repository.AdministradorRepository;
import com.isanorte.constructora_api.security.AdministradorUserDetailsService;
import com.isanorte.constructora_api.security.JwtService;

class AuthServiceTest {
    private AdministradorRepository repository;
    private AdministradorUserDetailsService users;
    private PasswordEncoder encoder;
    private JwtService jwt;
    private AuthService service;

    @BeforeEach
    void setUp() {
        repository = mock(AdministradorRepository.class);
        users = mock(AdministradorUserDetailsService.class);
        encoder = mock(PasswordEncoder.class);
        jwt = mock(JwtService.class);
        when(encoder.encode(anyString())).thenReturn("dummy-hash");
        service = new AuthService(repository, users, encoder, jwt,
                new AuthProperties("ADMINISTRADOR", "", ""));
    }

    @Test
    void autenticaAdminActivoSinExponerHash() {
        var admin = Administrador.builder().nombre("Admin").email("admin@example.test")
                .passwordHash("stored-hash").activo(true).build();
        admin.getRoles().add(Rol.builder().nombre("ADMINISTRADOR").activo(true).build());
        when(users.loadUserByUsername("admin@example.test")).thenReturn(User.withUsername(admin.getEmail())
                .password(admin.getPasswordHash()).authorities("ROLE_ADMINISTRADOR").build());
        when(encoder.matches("provided-password", "stored-hash")).thenReturn(true);
        when(repository.findByEmailIgnoreCase(admin.getEmail())).thenReturn(Optional.of(admin));
        when(jwt.emitir(eq(admin), anySet())).thenReturn(
                new JwtService.TokenEmitido("signed-token", Instant.parse("2030-01-01T00:00:00Z")));

        var response = service.login(new LoginRequest(admin.getEmail(), "provided-password"));

        assertEquals("signed-token", response.token());
        assertEquals(admin.getEmail(), response.usuario().email());
        verify(repository).save(admin);
    }

    @Test
    void respondeIgualCuandoElCorreoNoExiste() {
        when(users.loadUserByUsername(anyString())).thenThrow(new UsernameNotFoundException("no existe"));
        assertThrows(CredencialesInvalidasException.class,
                () -> service.login(new LoginRequest("unknown@example.test", "provided-password")));
        verify(encoder).matches("provided-password", "dummy-hash");
    }
}
