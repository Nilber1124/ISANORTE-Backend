package com.isanorte.constructora_api.service.implementation;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.isanorte.constructora_api.exception.ModelNotFoundException;
import com.isanorte.constructora_api.model.Producto;
import com.isanorte.constructora_api.repository.IGenericRepository;
import com.isanorte.constructora_api.repository.ProductoRepository;
import com.isanorte.constructora_api.service.IProductoService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductoService extends GenericService<Producto, UUID> implements IProductoService {

    private final ProductoRepository productoRepository;

    @Override
    protected IGenericRepository<Producto, UUID> getRepo() {
        return productoRepository;
    }

    @Override
    public Producto findBySlug(String slug) {
        return productoRepository.findBySlug(slug)
                .orElseThrow(() -> new ModelNotFoundException("Producto no encontrado con slug: " + slug));
    }

    @Override
    public Producto findBySku(String sku) {
        return productoRepository.findBySku(sku)
                .orElseThrow(() -> new ModelNotFoundException("Producto no encontrado con SKU: " + sku));
    }
}
