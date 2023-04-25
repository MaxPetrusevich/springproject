package com.spring.springproject.repositories;

import com.spring.springproject.entities.Category;
import com.spring.springproject.entities.Producer;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository

public interface ProducerRepository extends CrudRepository<Producer, Integer>, PagingAndSortingRepository<Producer, Integer> {
    Set<Producer> findByNameContaining(String name);
    Set<Producer> findByCountryContaining(String country);

}
