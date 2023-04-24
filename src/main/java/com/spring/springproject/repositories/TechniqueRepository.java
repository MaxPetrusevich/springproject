package com.spring.springproject.repositories;

import com.spring.springproject.entities.Technique;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository

public interface TechniqueRepository extends CrudRepository<Technique, Integer>, PagingAndSortingRepository<Technique, Integer> {
}
