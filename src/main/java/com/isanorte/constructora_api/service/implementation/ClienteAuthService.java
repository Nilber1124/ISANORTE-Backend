package com.isanorte.constructora_api.service.implementation;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.isanorte.constructora_api.dto.request.ClienteRegistroRequest;
import com.isanorte.constructora_api.dto.request.LoginRequest;
import com.isanorte.constructora_api.dto.response.ClienteAuthResponse;
import com.isanorte.constructora_api.dto.response.ClienteResponse;
import com.isanorte.constructora_api.exception.CredencialesInvalidasException;
import com.isanorte.constructora_api.exception.ModelNotFoundException;
import com.isanorte.constructora_api.model.Cliente;
import com.isanorte.constructora_api.repository.ClienteRepository;
import com.isanorte.constructora_api.security.JwtService;
import com.isanorte.constructora_api.service.IClienteAuthService;

@Service
public class ClienteAuthService implements IClienteAuthService {
    private final ClienteRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final String dummyHash;

    public ClienteAuthService(ClienteRepository repository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.dummyHash = passwordEncoder.encode(UUID.randomUUID().toString());
    }

    @Override
    @Transactional
    public ClienteAuthResponse registrar(ClienteRegistroRequest request) {
        String email = Cliente.normalizarEmail(request.email());
        if (repository.existsByEmail(email)) {
            throw new IllegalStateException("Ya existe una cuenta con este correo. Inicia sesión para continuar.");
        }

        LocalDateTime ahora = LocalDateTime.now();
        Cliente cliente = Cliente.builder()
                .nombre(request.nombre().trim())
                .apellido(blankToNull(request.apellido()))
                .email(email)
                .passwordHash(passwordEncoder.encode(request.password()))
                .telefono(blankToNull(request.telefono()))
                .activo(true)
                .consentimientoDatos(true)
                .fechaConsentimiento(ahora)
                .ultimoAcceso(ahora)
                .build();
        cliente = repository.save(cliente);
        return emitir(cliente);
    }

    @Override
    @Transactional
    public ClienteAuthResponse login(LoginRequest request) {
        String email = Cliente.normalizarEmail(request.email());
        var cliente = repository.findByEmail(email);
        if (cliente.isEmpty()) {
            passwordEncoder.matches(request.password(), dummyHash);
            throw new CredencialesInvalidasException();
        }

        Cliente encontrado = cliente.get();
        if (!Boolean.TRUE.equals(encontrado.getActivo())
                || !passwordEncoder.matches(request.password(), encontrado.getPasswordHash())) {
            throw new CredencialesInvalidasException();
        }

        encontrado.setUltimoAcceso(LocalDateTime.now());
        return emitir(repository.save(encontrado));
    }

    @Override
    @Transactional(readOnly = true)
    public ClienteResponse obtener(UUID clienteId) {
        return toResponse(repository.findById(clienteId)
                .orElseThrow(() -> new ModelNotFoundException("Cuenta de cliente no encontrada")));
    }

    private ClienteAuthResponse emitir(Cliente cliente) {
        var issued = jwtService.emitirCliente(cliente);
        return new ClienteAuthResponse(issued.valor(), "Bearer", issued.expiracion(), toResponse(cliente));
    }

    private ClienteResponse toResponse(Cliente cliente) {
        return new ClienteResponse(cliente.getId(), cliente.getNombre(), cliente.getApellido(),
                cliente.getEmail(), cliente.getTelefono(), cliente.getFechaCreacion());
    }

    private static String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
