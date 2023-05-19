package com.spring.springproject.service.interfaces;

import java.util.Set;

@org.springframework.stereotype.Service
public interface Service<T> {
    Set<T> findAll();

    T findById(Integer id);

    T save(T object);

    void update(T object);

    void delete(Integer id);
}
