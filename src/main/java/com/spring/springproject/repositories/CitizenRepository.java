package com.spring.springproject.repositories;

import com.spring.springproject.entities.Citizen;
import com.spring.springproject.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CitizenRepository extends JpaRepository<Citizen, Long>, JpaSpecificationExecutor<Citizen> {

    // Dynamic query execution with conditions
    @Query("SELECT c FROM Citizen c WHERE c.firstName LIKE %:name%")
    List<Citizen> findByFirstNameContaining(@Param("name") String name);

    Citizen findByIdentifyNumber(String identity);

    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Citizen c WHERE c.email = :email")
    boolean existsByEmail(@Param("email") String email);

    Citizen findByEmail(String email);

    Optional<Citizen> findByUser(User user);
}
