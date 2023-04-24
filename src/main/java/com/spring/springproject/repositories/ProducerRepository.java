package com.spring.springproject.repositories;

import com.spring.springproject.entities.Producer;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository

public interface ProducerRepository extends CrudRepository<Producer, Integer>, PagingAndSortingRepository<Producer, Integer> {
}
