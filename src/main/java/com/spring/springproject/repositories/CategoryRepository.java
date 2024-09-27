package com.spring.springproject.repositories;

import com.spring.springproject.entities.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.OrderBy;
import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository  extends JpaRepository<Category, Integer> {


    @Transactional
    @Modifying
    @Query(value = "INSERT INTO Category (category_name) VALUES (:#{#category.name})", nativeQuery = true)
    void saveCategory(@Param("category") Category category);
    @Query("SELECT c FROM Category c WHERE c.id = :id")
    Optional<Category> findById(@Param("id") Integer id);
    @OrderBy("id")
    @Query("SELECT c FROM Category c")
    List<Category> findAll();
    @Query("UPDATE Category c SET c.name = :#{#category.name} WHERE c.id = :#{#category.id}")
    @Modifying
    @Transactional
    void update(@Param("category") Category category);
    @Query("DELETE FROM Category c WHERE c.id = :id")
    @Modifying
    @Transactional
    void deleteById(@Param("id")Integer id);
    @OrderBy("id")
    @Query(value = "SELECT c FROM Category c WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :#{#category.name}, '%'))",
            countQuery = "SELECT COUNT(c) FROM Category c WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :#{#category.name}, '%'))")
    Page<Category> findAll(@Param("category") Category category, Pageable pageable);

}
