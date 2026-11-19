package com.isanorte.constructora_api.service;

import java.util.List;
import java.util.UUID;

import com.isanorte.constructora_api.dto.request.AjusteStockRequest;
import com.isanorte.constructora_api.dto.response.MovimientoStockResponse;
import com.isanorte.constructora_api.dto.response.StockResponse;

public interface IStockService {

    StockResponse ajustarProducto(UUID productoId, AjusteStockRequest request, String usuario);

    StockResponse ajustarVariante(UUID productoId, UUID varianteId, AjusteStockRequest request, String usuario);

    List<MovimientoStockResponse> historial(UUID productoId);
}
