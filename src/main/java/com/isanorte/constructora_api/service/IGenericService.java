package com.isanorte.constructora_api.service;

import java.util.List;

public interface IGenericService<T, ID> {

    T save(T t);

    List<T> findAll();

    T findById(ID id);
}
