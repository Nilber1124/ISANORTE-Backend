package com.isanorte.constructora_api.dto.response;

import java.util.List;

import com.isanorte.constructora_api.dto.request.CarritoItemDto;

public record CarritoClienteResponse(List<CarritoItemDto> items) {
}
