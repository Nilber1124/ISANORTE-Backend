package com.isanorte.constructora_api.service;

import java.util.UUID;

import com.isanorte.constructora_api.model.Rol;

public interface IRolService extends IGenericService<Rol, UUID> {

    Rol findByNombre(String nombre);
}
