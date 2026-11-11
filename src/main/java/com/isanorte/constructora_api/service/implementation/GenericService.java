package com.isanorte.constructora_api.service.implementation;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import com.isanorte.constructora_api.exception.ModelNotFoundException;
import com.isanorte.constructora_api.repository.IGenericRepository;
import com.isanorte.constructora_api.service.IGenericService;

@Transactional(readOnly = true)
public abstract class GenericService<T, I> implements IGenericService<T, I> {

    protected abstract IGenericRepository<T, I> getRepo();

    @Override
    @Transactional
    public T save(T t) {
        return getRepo().save(t);
    }

    @Override
    public List<T> findAll() {
        return getRepo().findAll();
    }

    @Override
    public T findById(I id) {
        return getRepo().findById(id)
                .orElseThrow(() -> new ModelNotFoundException("Registro no encontrado con ID: " + id));
    }
}
