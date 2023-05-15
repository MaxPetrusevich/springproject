package com.spring.springproject.repositories;

import com.spring.springproject.entities.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository

public interface StoreRepository extends JpaRepository<Store, Integer>, JpaSpecificationExecutor<Store> {


}
