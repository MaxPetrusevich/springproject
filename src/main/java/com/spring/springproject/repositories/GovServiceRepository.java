package com.spring.springproject.repositories;

import com.spring.springproject.entities.GovService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GovServiceRepository extends JpaRepository<GovService, Long> {

    @Query("SELECT s FROM GovService s LEFT JOIN FETCH s.category LEFT JOIN FETCH s.establishment")
    List<GovService> findAllWithDetails();

    Page<GovService> findByCategoryId(Long categoryId, Pageable pageable);
    Page<GovService> findByEstablishmentId(Long establishmentId, Pageable pageable);
    Page<GovService> findByCategoryIdAndEstablishmentId(Long categoryId, Long establishmentId, Pageable pageable);

    Page<GovService> findByNameContainingIgnoreCaseAndCategoryId(String name, Long categoryId, Pageable pageable);

    Page<GovService> findByNameContainingIgnoreCaseAndEstablishmentId(String name, Long establishmentId, Pageable pageable);

    Page<GovService> findByNameContainingIgnoreCaseAndCategoryIdAndEstablishmentId(
            String name, Long categoryId, Long establishmentId, Pageable pageable);

    @Query("SELECT g FROM GovService g")
    Page<GovService> findAllPaged(Pageable pageable);

    @Query("SELECT s FROM GovService s WHERE LOWER(s.name) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<GovService> findByNameContainingIgnoreCase(@Param("search") String search, Pageable pageable);
}
