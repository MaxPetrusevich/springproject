package com.spring.springproject.repositories;

import com.spring.springproject.entities.Type;
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
public interface TypeRepository extends JpaRepository<Type, Integer> {

    @Transactional
    @Modifying
    @Query(value = "INSERT INTO Type (name, category_id) VALUES (:#{#type.name}, :#{#type.category.id})", nativeQuery = true)
    void saveType(@Param("type") Type type);

    @Query("SELECT t FROM Type t WHERE t.id = :id")
    Optional<Type> findById(@Param("id") Integer id);

    @Query("SELECT t FROM Type t ORDER BY t.id")
    List<Type> findAll();

    @Query("UPDATE Type t SET t.name = :#{#type.name}, t.category = :#{#type.category} WHERE t.id = :#{#type.id}")
    @Modifying
    @Transactional
    void update(@Param("type") Type type);

    @Query("DELETE FROM Type t WHERE t.id = :id")
    @Modifying
    @Transactional
    void deleteById(@Param("id") Integer id);

    @Query(value = "SELECT t FROM Type t WHERE LOWER(t.name) LIKE LOWER(CONCAT('%', :#{#type.name}, '%')) " +
            "AND LOWER(t.category.name) LIKE LOWER(CONCAT('%', :#{#type.category.name}, '%')) ORDER BY t.id DESC ",
            countQuery = "SELECT COUNT(t) FROM Type t WHERE LOWER(t.name) LIKE LOWER(CONCAT('%', :#{#type.name}, '%')) " +
                    "AND LOWER(t.category.name) LIKE LOWER(CONCAT('%', :#{#type.category.name}, '%'))")
    Page<Type> findAll(@Param("type") Type type, Pageable pageable);
}
