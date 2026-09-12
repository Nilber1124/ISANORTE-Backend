package com.isanorte.constructora_api.service;

import java.util.UUID;

import com.isanorte.constructora_api.model.Proyecto;

public interface IProyectoService extends IGenericService<Proyecto, UUID> {

    Proyecto findBySlug(String slug);
}
