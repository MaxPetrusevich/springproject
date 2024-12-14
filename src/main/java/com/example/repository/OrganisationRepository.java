package com.example.repository;

import com.example.entity.Organisation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrganisationRepository extends JpaRepository<Organisation, Long> {
    List<Organisation> findByOwnerId(Long ownerId);
    
    @Query("SELECT o FROM Organisation o LEFT JOIN FETCH o.products WHERE o.id = :id")
    Organisation findByIdWithProducts(@Param("id") Long id);
    
    boolean existsByNameIgnoreCase(String name);
    
    @Query("SELECT COUNT(o) FROM Organisation o WHERE o.active = true")
    long countByActiveTrue();
    
    List<Organisation> findTop5ByOrderByCreatedAtDesc();
    
    Page<Organisation> findByNameContainingOrDescriptionContaining(
        String name, 
        String description, 
        Pageable pageable
    );
    
    Page<Organisation> findByNameContainingAndActive(
        String name, 
        boolean active, 
        Pageable pageable
    );
    
    Page<Organisation> findByNameContaining(
        String name, 
        Pageable pageable
    );
    
    Page<Organisation> findByActive(
        boolean active, 
        Pageable pageable
    );
    
    @Query("SELECT COUNT(o) FROM Organisation o WHERE o.active = :active")
    long countByActive(@Param("active") boolean active);
    
    @Query("SELECT COUNT(o) FROM Organisation o WHERE o.createdAt >= :date")
    long countByCreatedAtAfter(@Param("date") LocalDateTime date);
    
    @Query(value = """
        SELECT DATE_TRUNC('month', created_at) as date, COUNT(*) as count 
        FROM organisations 
        GROUP BY DATE_TRUNC('month', created_at) 
        ORDER BY date DESC 
        LIMIT 12
        """, 
        nativeQuery = true)
    List<Object[]> countOrganisationsByMonth();
    
    boolean existsByIdAndOwnerId(Long id, Long ownerId);
    
    @Query("SELECT COUNT(o) FROM Organisation o WHERE o.owner.id = :ownerId")
    long countByOwnerId(@Param("ownerId") Long ownerId);
} 