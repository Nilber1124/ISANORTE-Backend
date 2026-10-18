package com.isanorte.constructora_api.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.isanorte.constructora_api.dto.request.CarritoClienteRequest;
import com.isanorte.constructora_api.dto.request.ClienteRegistroRequest;
import com.isanorte.constructora_api.dto.request.DireccionClienteRequest;
import com.isanorte.constructora_api.dto.request.LoginRequest;
import com.isanorte.constructora_api.dto.response.ClienteAuthResponse;
import com.isanorte.constructora_api.dto.response.CarritoClienteResponse;
import com.isanorte.constructora_api.dto.response.ClienteResponse;
import com.isanorte.constructora_api.dto.response.DireccionClienteResponse;
import com.isanorte.constructora_api.dto.response.PublicCotizacionResponse;
import com.isanorte.constructora_api.dto.response.PublicPedidoResponse;
import com.isanorte.constructora_api.service.ICotizacionService;
import com.isanorte.constructora_api.service.ICarritoClienteService;
import com.isanorte.constructora_api.service.IClienteAuthService;
import com.isanorte.constructora_api.service.IDireccionClienteService;
import com.isanorte.constructora_api.service.IPedidoService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/publico/cuenta")
@RequiredArgsConstructor
public class CuentaPublicaController {
    private final IClienteAuthService clienteAuthService;
    private final ICotizacionService cotizacionService;
    private final IPedidoService pedidoService;
    private final ICarritoClienteService carritoService;
    private final IDireccionClienteService direccionService;

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

    @GetMapping("/pedidos")
    public ResponseEntity<List<PublicPedidoResponse>> misPedidos(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok().cacheControl(CacheControl.noStore())
                .body(pedidoService.findPublicByCliente(UUID.fromString(jwt.getSubject())));
    }

    @GetMapping("/direcciones")
    public ResponseEntity<List<DireccionClienteResponse>> misDirecciones(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok().cacheControl(CacheControl.noStore())
                .body(direccionService.findByCliente(UUID.fromString(jwt.getSubject())));
    }

    @PostMapping("/direcciones")
    public ResponseEntity<DireccionClienteResponse> crearDireccion(@AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody DireccionClienteRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).cacheControl(CacheControl.noStore())
                .body(direccionService.create(UUID.fromString(jwt.getSubject()), request));
    }

    @PutMapping("/direcciones/{id}")
    public ResponseEntity<DireccionClienteResponse> actualizarDireccion(@AuthenticationPrincipal Jwt jwt,
            @PathVariable UUID id, @Valid @RequestBody DireccionClienteRequest request) {
        return ResponseEntity.ok().cacheControl(CacheControl.noStore())
                .body(direccionService.update(UUID.fromString(jwt.getSubject()), id, request));
    }

    @DeleteMapping("/direcciones/{id}")
    public ResponseEntity<Void> eliminarDireccion(@AuthenticationPrincipal Jwt jwt, @PathVariable UUID id) {
        direccionService.delete(UUID.fromString(jwt.getSubject()), id);
        return ResponseEntity.noContent().build();
    }
}
