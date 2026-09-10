package com.isanorte.constructora_api.service.implementation;

import java.util.List;

import com.isanorte.constructora_api.service.IGenericService;

public abstract class GenericService<T, ID> implements IGenericService<T, ID> {
    @Override
    public T save(T t) throws Exception {
        throw new UnsupportedOperationException("Unimplemented method 'save'");
    }

    @Override
    public T update(T t, ID id) throws Exception {
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }

    @Override
    public List<T> findAll() throws Exception {
        throw new UnsupportedOperationException("Unimplemented method 'findAll'");
    }

    @Override
    public T findById(ID id) throws Exception {
        throw new UnsupportedOperationException("Unimplemented method 'findById'");
    }

    @Override
    public void delete(ID id) throws Exception {
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }

}
