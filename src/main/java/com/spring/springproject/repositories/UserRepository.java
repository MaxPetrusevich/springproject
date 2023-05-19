package com.spring.springproject.repositories;

import com.spring.springproject.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository

public interface UserRepository extends JpaRepository<User,Integer>, JpaSpecificationExecutor<User> {
    User findByUserName(String userName);
}
