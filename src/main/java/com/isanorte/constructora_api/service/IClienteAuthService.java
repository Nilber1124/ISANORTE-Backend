package com.isanorte.constructora_api.service;

import java.util.UUID;

import com.isanorte.constructora_api.dto.request.ClienteRegistroRequest;
import com.isanorte.constructora_api.dto.request.LoginRequest;
import com.isanorte.constructora_api.dto.response.ClienteAuthResponse;
import com.isanorte.constructora_api.dto.response.ClienteResponse;

public interface IClienteAuthService {

    ClienteAuthResponse registrar(ClienteRegistroRequest request);

    ClienteAuthResponse login(LoginRequest request);

    ClienteResponse obtener(UUID clienteId);
}
