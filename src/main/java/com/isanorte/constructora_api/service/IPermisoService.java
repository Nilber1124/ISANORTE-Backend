package com.isanorte.constructora_api.service;

import java.util.UUID;

import com.isanorte.constructora_api.model.Permiso;

public interface IPermisoService extends IGenericService<Permiso, UUID> {

    Permiso findByCodigo(String codigo);
}
