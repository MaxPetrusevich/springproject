package com.spring.springproject.repositories;

import com.spring.springproject.entities.Producer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository

public interface ProducerRepository extends JpaRepository<Producer, Integer>, JpaSpecificationExecutor<Producer> {

}
