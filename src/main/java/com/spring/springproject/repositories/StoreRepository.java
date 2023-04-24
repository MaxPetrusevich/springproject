package com.spring.springproject.repositories;

import com.spring.springproject.entities.Store;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository

public interface StoreRepository extends CrudRepository<Store, Integer>, PagingAndSortingRepository<Store, Integer> {
}
