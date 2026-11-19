package com.isanorte.constructora_api.service.implementation;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.isanorte.constructora_api.dto.request.SeccionProductosIdsRequest;
import com.isanorte.constructora_api.dto.response.SeccionProductoItemResponse;
import com.isanorte.constructora_api.exception.ModelNotFoundException;
import com.isanorte.constructora_api.model.IsadecorSeccionExcluido;
import com.isanorte.constructora_api.model.IsadecorSeccionFijado;
import com.isanorte.constructora_api.model.IsadecorSeccionProductos;
import com.isanorte.constructora_api.model.Producto;
import com.isanorte.constructora_api.repository.IsadecorSeccionExcluidoRepository;
import com.isanorte.constructora_api.repository.IsadecorSeccionFijadoRepository;
import com.isanorte.constructora_api.repository.IsadecorSeccionProductosRepository;
import com.isanorte.constructora_api.repository.ProductoRepository;
import com.isanorte.constructora_api.service.IIsadecorSeccionSeleccionService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class IsadecorSeccionSeleccionService implements IIsadecorSeccionSeleccionService {

    private final IsadecorSeccionProductosRepository seccionRepository;
    private final IsadecorSeccionFijadoRepository fijadoRepository;
    private final IsadecorSeccionExcluidoRepository excluidoRepository;
    private final ProductoRepository productoRepository;

    @Override
    public List<SeccionProductoItemResponse> fijados(UUID seccionId) {
        findSeccion(seccionId);
        return fijadoRepository.findBySeccionIdOrderByOrdenAscIdAsc(seccionId).stream()
                .map(f -> item(f.getProducto(), f.getOrden()))
                .toList();
    }

    @Override
    @Transactional
    public List<SeccionProductoItemResponse> reemplazarFijados(UUID seccionId, SeccionProductosIdsRequest request) {
        IsadecorSeccionProductos seccion = findSeccion(seccionId);
        Map<UUID, Producto> productos = resolverProductos(request.productoIds());
        fijadoRepository.deleteBySeccionId(seccionId);
        fijadoRepository.flush();
        int orden = 0;
        for (UUID productoId : request.productoIds()) {
            fijadoRepository.save(IsadecorSeccionFijado.builder()
                    .seccion(seccion).producto(productos.get(productoId)).orden(orden++).build());
        }
        return fijadoRepository.findBySeccionIdOrderByOrdenAscIdAsc(seccionId).stream()
                .map(f -> item(f.getProducto(), f.getOrden()))
                .toList();
    }

    @Override
    public List<SeccionProductoItemResponse> excluidos(UUID seccionId) {
        findSeccion(seccionId);
        return excluidoRepository.findBySeccionId(seccionId).stream()
                .map(e -> item(e.getProducto(), null))
                .toList();
    }

    @Override
    @Transactional
    public List<SeccionProductoItemResponse> reemplazarExcluidos(UUID seccionId, SeccionProductosIdsRequest request) {
        IsadecorSeccionProductos seccion = findSeccion(seccionId);
        Map<UUID, Producto> productos = resolverProductos(request.productoIds());
        excluidoRepository.deleteBySeccionId(seccionId);
        excluidoRepository.flush();
        for (UUID productoId : request.productoIds()) {
            excluidoRepository.save(IsadecorSeccionExcluido.builder()
                    .seccion(seccion).producto(productos.get(productoId)).build());
        }
        return excluidoRepository.findBySeccionId(seccionId).stream()
                .map(e -> item(e.getProducto(), null))
                .toList();
    }

    private IsadecorSeccionProductos findSeccion(UUID id) {
        return seccionRepository.findById(id)
                .orElseThrow(() -> new ModelNotFoundException("Sección de productos no encontrada con ID: " + id));
    }

    /** Valida que no haya repetidos y que todos los productos existan. */
    private Map<UUID, Producto> resolverProductos(List<UUID> ids) {
        Set<UUID> vistos = new HashSet<>();
        for (UUID id : ids) {
            if (!vistos.add(id)) {
                throw new IllegalArgumentException("El producto " + id + " está repetido en la lista");
            }
        }
        Map<UUID, Producto> encontrados = productoRepository.findAllById(ids).stream()
                .collect(Collectors.toMap(Producto::getId, Function.identity()));
        for (UUID id : ids) {
            if (!encontrados.containsKey(id)) {
                throw new ModelNotFoundException("Producto no encontrado con ID: " + id);
            }
        }
        return encontrados;
    }

    private SeccionProductoItemResponse item(Producto producto, Integer orden) {
        return new SeccionProductoItemResponse(producto.getId(), producto.getNombre(), producto.getSlug(), orden);
    }
}
