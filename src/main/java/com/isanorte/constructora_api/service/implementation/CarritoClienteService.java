package com.isanorte.constructora_api.service.implementation;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.isanorte.constructora_api.dto.request.CarritoClienteRequest;
import com.isanorte.constructora_api.dto.request.CarritoItemDto;
import com.isanorte.constructora_api.dto.response.CarritoClienteResponse;
import com.isanorte.constructora_api.exception.ModelNotFoundException;
import com.isanorte.constructora_api.model.CarritoCliente;
import com.isanorte.constructora_api.repository.CarritoClienteRepository;
import com.isanorte.constructora_api.repository.ClienteRepository;
import com.isanorte.constructora_api.service.ICarritoClienteService;

import lombok.RequiredArgsConstructor;
import tools.jackson.databind.ObjectMapper;

@Service
@RequiredArgsConstructor
public class CarritoClienteService implements ICarritoClienteService {
    private static final int MAX_CANTIDAD = 999_999;

    private final CarritoClienteRepository repository;
    private final ClienteRepository clienteRepository;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional(readOnly = true)
    public CarritoClienteResponse obtener(UUID clienteId) {
        return new CarritoClienteResponse(repository.findById(clienteId)
                .map(carrito -> deserializar(carrito.getContenido()))
                .orElseGet(List::of));
    }

    @Override
    @Transactional
    public CarritoClienteResponse reemplazar(UUID clienteId, CarritoClienteRequest request) {
        if (!clienteRepository.existsById(clienteId)) {
            throw new ModelNotFoundException("Cuenta de cliente no encontrada");
        }
        List<CarritoItemDto> items = consolidar(request.items());
        CarritoCliente carrito = repository.findById(clienteId)
                .orElseGet(() -> CarritoCliente.builder().clienteId(clienteId).build());
        carrito.setContenido(serializar(items));
        repository.save(carrito);
        return new CarritoClienteResponse(items);
    }

    /** Une líneas repetidas (mismo producto y variante) sumando cantidades, con tope de MAX_CANTIDAD. */
    private List<CarritoItemDto> consolidar(List<CarritoItemDto> entrada) {
        Map<String, CarritoItemDto> porClave = new LinkedHashMap<>();
        for (CarritoItemDto item : entrada) {
            String clave = item.productoSlug() + "::" + (item.variante() == null ? "" : item.variante().sku());
            porClave.merge(clave, item, (actual, nuevo) -> new CarritoItemDto(
                    actual.productoSlug(), actual.productoNombre(), actual.productoSku(),
                    actual.imagenUrl(), actual.imagenAlt(), actual.variante(),
                    Math.min(actual.cantidad() + nuevo.cantidad(), MAX_CANTIDAD), actual.precioUnitario()));
        }
        return new ArrayList<>(porClave.values());
    }

    private String serializar(List<CarritoItemDto> items) {
        return objectMapper.writeValueAsString(items);
    }

    private List<CarritoItemDto> deserializar(String contenido) {
        try {
            return List.of(objectMapper.readValue(contenido, CarritoItemDto[].class));
        } catch (RuntimeException exception) {
            // Contenido ilegible: se devuelve vacío en lugar de romper la tienda; el siguiente guardado lo reemplaza.
            return List.of();
        }
    }
}
