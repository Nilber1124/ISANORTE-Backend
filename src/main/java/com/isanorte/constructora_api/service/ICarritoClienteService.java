package com.isanorte.constructora_api.service;

import java.util.UUID;

import com.isanorte.constructora_api.dto.request.CarritoClienteRequest;
import com.isanorte.constructora_api.dto.response.CarritoClienteResponse;

public interface ICarritoClienteService {

    CarritoClienteResponse obtener(UUID clienteId);

    CarritoClienteResponse reemplazar(UUID clienteId, CarritoClienteRequest request);
}
