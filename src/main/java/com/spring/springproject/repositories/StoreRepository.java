package com.spring.springproject.repositories;

import com.spring.springproject.entities.Store;
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
public interface StoreRepository extends JpaRepository<Store, Integer> {

    @Transactional
    @Modifying
    @Query(value = "INSERT INTO Store (name, address) VALUES (:#{#store.name}, :#{#store.address})", nativeQuery = true)
    void saveStore(@Param("store") Store store);

    @Query("SELECT s FROM Store s WHERE s.id = :id")
    Optional<Store> findById(@Param("id") Integer id);

    @Query("SELECT s FROM Store s ORDER BY s.id")
    List<Store> findAll();

    @Query("UPDATE Store s SET s.name = :#{#store.name}, s.address = :#{#store.address} WHERE s.id = :#{#store.id}")
    @Modifying
    @Transactional
    void update(@Param("store") Store store);

    @Query("DELETE FROM Store s WHERE s.id = :id")
    @Modifying
    @Transactional
    void deleteById(@Param("id") Integer id);

    @Query(value = "SELECT s FROM Store s WHERE LOWER(s.name) LIKE LOWER(CONCAT('%', :#{#store.name}, '%')) " +
            "AND LOWER(s.address) LIKE LOWER(CONCAT('%', :#{#store.address}, '%')) ORDER BY s.id DESC ",
            countQuery = "SELECT COUNT(s) FROM Store s WHERE LOWER(s.name) LIKE LOWER(CONCAT('%', :#{#store.name}, '%')) " +
                    "AND LOWER(s.address) LIKE LOWER(CONCAT('%', :#{#store.address}, '%'))")
    Page<Store> findAll(@Param("store") Store store, Pageable pageable);
}
