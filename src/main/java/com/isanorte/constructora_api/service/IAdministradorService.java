package com.isanorte.constructora_api.service;

import java.util.UUID;

import com.isanorte.constructora_api.model.Administrador;

public interface IAdministradorService extends IGenericService<Administrador, UUID> {

    Administrador findByEmail(String email);
}
