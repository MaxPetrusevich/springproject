package com.spring.springproject.repositories;

import com.spring.springproject.entities.Category;
import com.spring.springproject.entities.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository

public interface StoreRepository extends JpaRepository<Store, Integer>, JpaSpecificationExecutor<Store> {
    Set<Store> findByNameContaining(String name);
    Set<Store> findByAddressContaining(String address);

}
