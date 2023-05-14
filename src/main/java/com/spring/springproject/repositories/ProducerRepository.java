package com.spring.springproject.repositories;

import com.spring.springproject.entities.Category;
import com.spring.springproject.entities.Producer;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.awt.print.Pageable;
import java.util.Set;

@Repository

public interface ProducerRepository extends JpaRepository<Producer, Integer>, JpaSpecificationExecutor<Producer> {

}
