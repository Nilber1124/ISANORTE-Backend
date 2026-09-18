package com.isanorte.constructora_api.repository;

import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.isanorte.constructora_api.model.DocumentoProducto;

@Repository
public interface DocumentoProductoRepository extends IGenericRepository<DocumentoProducto, UUID> {
}
