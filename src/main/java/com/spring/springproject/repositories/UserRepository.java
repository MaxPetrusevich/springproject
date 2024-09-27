package com.spring.springproject.repositories;

import com.spring.springproject.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository

public interface UserRepository extends JpaRepository<User,Integer>, JpaSpecificationExecutor<User> {
    @Query("select u from User u where u.userName = :userName")
    User findByUserName(@Param("userName") String userName);
    @Transactional
    @Modifying
    @Query(value = "INSERT INTO users (user_name, password, image, name, surname, email) " +
            "VALUES (:#{#user.userName}, :#{#user.password}, :#{#user.image}, :#{#user.name}, :#{#user.surname}, :#{#user.email})", nativeQuery = true)
    void saveUser(@Param("user") User user);

    @Query("SELECT u FROM User u WHERE u.id = :id")
    Optional<User> findById(@Param("id") Integer id);

    @Query("SELECT u FROM User u ORDER BY u.id")
    List<User> findAll();

    @Query("UPDATE User u SET u.userName = :#{#user.userName}, u.password = :#{#user.password}, " +
            "u.image = :#{#user.image}, u.name = :#{#user.name}, u.surname = :#{#user.surname}, u.email = :#{#user.email} WHERE u.id = :#{#user.id}")
    @Modifying
    @Transactional
    void update(@Param("user") User user);

    @Query("DELETE FROM User u WHERE u.id = :id")
    @Modifying
    @Transactional
    void deleteById(@Param("id") Integer id);
    /*@Query("SELECT u FROM User u WHERE " +
            "LOWER(u.name) LIKE LOWER(CONCAT('%', :#{#user.name}, '%')) " +
            "AND LOWER(u.surname) LIKE LOWER(CONCAT('%', :#{#user.surname}, '%')) " +
            "AND LOWER(u.email) LIKE LOWER(CONCAT('%', :#{#user.email}, '%')) " +
            "ORDER BY u.id DESC")
    Page<User> findAll(@Param("user") User user, Pageable pageable);*/
    Page<User> findAll(Specification<User> specification, Pageable pageable);
}
