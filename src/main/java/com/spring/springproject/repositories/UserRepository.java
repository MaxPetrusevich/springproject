package com.spring.springproject.repositories;

import com.spring.springproject.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    @Query("SELECT u FROM User u WHERE u.identifyNumber = :identifyNumber")
    User findByIdentifyNumber(String identifyNumber);


    boolean existsByIdentifyNumber(String identifyNumber);
}
