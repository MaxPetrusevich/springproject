package com.spring.springproject.repositories;

import com.spring.springproject.entities.Category;
import com.spring.springproject.entities.Type;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository

public interface TypeRepository extends CrudRepository<Type, Integer>, PagingAndSortingRepository<Type, Integer> {
    Set<Type> findByNameContaining(String name);

}
