package com.spring.springproject.repositories;

import com.spring.springproject.entities.Type;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TypeRepository extends JpaRepository<Type, Long>, JpaSpecificationExecutor<Type> {

    @Query("SELECT t FROM Type t WHERE t.type = :code")
    Type findByType(String code);
}
