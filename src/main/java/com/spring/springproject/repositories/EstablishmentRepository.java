package com.spring.springproject.repositories;

import com.spring.springproject.entities.Establishment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EstablishmentRepository extends JpaRepository<Establishment, Long>, JpaSpecificationExecutor<Establishment> {

    // Dynamic query execution with conditions
    @Query("SELECT e FROM Establishment e WHERE e.name LIKE %:name%")
    List<Establishment> findByNameContaining(@Param("name") String name);
}
