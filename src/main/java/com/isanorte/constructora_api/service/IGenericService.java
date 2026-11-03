package com.isanorte.constructora_api.service;

import java.util.List;

public interface IGenericService<T, I> {

    T save(T t);

    List<T> findAll();

    T findById(I id);
}
