package com.isanorte.constructora_api.repository;

import java.util.List;
import java.util.UUID;

import com.isanorte.constructora_api.model.MovimientoStock;

public interface MovimientoStockRepository extends IGenericRepository<MovimientoStock, UUID> {

    List<MovimientoStock> findByProductoIdOrderByFechaDescIdDesc(UUID productoId);
}
