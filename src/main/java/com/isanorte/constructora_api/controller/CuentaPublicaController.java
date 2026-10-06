package com.isanorte.constructora_api.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.isanorte.constructora_api.dto.request.CarritoClienteRequest;
import com.isanorte.constructora_api.dto.request.ClienteRegistroRequest;
import com.isanorte.constructora_api.dto.request.LoginRequest;
import com.isanorte.constructora_api.dto.response.ClienteAuthResponse;
import com.isanorte.constructora_api.dto.response.CarritoClienteResponse;
import com.isanorte.constructora_api.dto.response.ClienteResponse;
import com.isanorte.constructora_api.dto.response.PublicCotizacionResponse;
import com.isanorte.constructora_api.service.ICotizacionService;
import com.isanorte.constructora_api.service.ICarritoClienteService;
import com.isanorte.constructora_api.service.IClienteAuthService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/publico/cuenta")
@RequiredArgsConstructor
public class CuentaPublicaController {
    private final IClienteAuthService clienteAuthService;
    private final ICotizacionService cotizacionService;
    private final ICarritoClienteService carritoService;

    @PostMapping("/registro")
    public ResponseEntity<ClienteAuthResponse> registrar(@Valid @RequestBody ClienteRegistroRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .cacheControl(CacheControl.noStore())
                .body(clienteAuthService.registrar(request));
    }

    @PostMapping("/login")
    public ResponseEntity<ClienteAuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok().cacheControl(CacheControl.noStore()).body(clienteAuthService.login(request));
    }

    @GetMapping("/me")
    public ResponseEntity<ClienteResponse> me(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok().cacheControl(CacheControl.noStore())
                .body(clienteAuthService.obtener(UUID.fromString(jwt.getSubject())));
    }

    @GetMapping("/cotizaciones")
    public ResponseEntity<List<PublicCotizacionResponse>> misCotizaciones(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok().cacheControl(CacheControl.noStore())
                .body(cotizacionService.findPublicByCliente(UUID.fromString(jwt.getSubject())));
    }

    @GetMapping("/carrito")
    public ResponseEntity<CarritoClienteResponse> obtenerCarrito(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok().cacheControl(CacheControl.noStore())
                .body(carritoService.obtener(UUID.fromString(jwt.getSubject())));
    }

    @PutMapping("/carrito")
    public ResponseEntity<CarritoClienteResponse> reemplazarCarrito(@AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody CarritoClienteRequest request) {
        return ResponseEntity.ok().cacheControl(CacheControl.noStore())
                .body(carritoService.reemplazar(UUID.fromString(jwt.getSubject()), request));
    }
}
