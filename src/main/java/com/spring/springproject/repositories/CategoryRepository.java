package com.spring.springproject.repositories;

import com.spring.springproject.entities.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    Page<Category> findByCategoryContainingIgnoreCase(String category, Pageable pageable);

    // Custom method for searching categories with filtering
    @Query("SELECT c FROM Category c WHERE c.category LIKE %:name%")
    List<Category> findByNameContaining(@Param("name") String name);
}
