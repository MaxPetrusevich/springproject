package com.spring.springproject.repositories;

import com.spring.springproject.entities.Producer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProducerRepository extends JpaRepository<Producer, Integer> {

    @Transactional
    @Modifying
    @Query(value = "INSERT INTO Producer (name, country) VALUES (:#{#producer.name}, :#{#producer.country})", nativeQuery = true)
    void saveProducer(@Param("producer") Producer producer);

    @Query("SELECT p FROM Producer p WHERE p.id = :id")
    Optional<Producer> findById(@Param("id") Integer id);

    @Query("SELECT p FROM Producer p ORDER BY p.id")
    List<Producer> findAll();

    @Query("UPDATE Producer p SET p.name = :#{#producer.name}, p.country = :#{#producer.country} WHERE p.id = :#{#producer.id}")
    @Modifying
    @Transactional
    void update(@Param("producer") Producer producer);

    @Query("DELETE FROM Producer p WHERE p.id = :id")
    @Modifying
    @Transactional
    void deleteById(@Param("id") Integer id);

    @Query(value = "SELECT p FROM Producer p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :#{#producer.name}, '%')) " +
            "AND LOWER(p.country) LIKE LOWER(CONCAT('%', :#{#producer.country}, '%')) ORDER BY p.id DESC ",
            countQuery = "SELECT COUNT(p) FROM Producer p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :#{#producer.name}, '%')) " +
                    "AND LOWER(p.country) LIKE LOWER(CONCAT('%', :#{#producer.country}, '%'))")
    Page<Producer> findAll(@Param("producer") Producer producer, Pageable pageable);
}
