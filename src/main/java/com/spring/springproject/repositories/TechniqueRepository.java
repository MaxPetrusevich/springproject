package com.spring.springproject.repositories;

import com.spring.springproject.entities.Technique;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository

public interface TechniqueRepository extends CrudRepository<Technique, Integer>, PagingAndSortingRepository<Technique, Integer> {
    Set<Technique> findByPriceBetween(Double startPrice, Double endPrice);
}
