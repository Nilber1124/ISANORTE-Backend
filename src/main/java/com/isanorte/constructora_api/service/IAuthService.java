package com.isanorte.constructora_api.service;

import com.isanorte.constructora_api.dto.request.LoginRequest;
import com.isanorte.constructora_api.dto.response.LoginResponse;

public interface IAuthService {
    LoginResponse login(LoginRequest request);
}
