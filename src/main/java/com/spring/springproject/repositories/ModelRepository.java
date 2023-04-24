package com.spring.springproject.repositories;

import com.spring.springproject.entities.Model;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository

public interface ModelRepository extends CrudRepository<Model, Integer>, PagingAndSortingRepository<Model, Integer> {
}
