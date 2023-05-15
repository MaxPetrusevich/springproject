package com.spring.springproject.repositories;

import com.spring.springproject.entities.Technique;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository

public interface TechniqueRepository extends JpaRepository<Technique, Integer>, JpaSpecificationExecutor<Technique> {
}
