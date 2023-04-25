package com.spring.springproject.repositories;

import com.spring.springproject.entities.Model;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository

public interface ModelRepository extends CrudRepository<Model, Integer>, PagingAndSortingRepository<Model, Integer> {
    Set<Model> findByNameContaining(String name);

}
