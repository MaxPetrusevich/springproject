package com.spring.springproject.repositories;

import com.spring.springproject.entities.Model;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository

public interface ModelRepository extends JpaRepository<Model, Integer>, JpaSpecificationExecutor<Model> {

}
