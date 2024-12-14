package com.example.repository;

import com.example.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findTop6ByOrderByCreatedAtDesc();

    Page<Product> findByCategory(String category, Pageable pageable);
    
    Page<Product> findByNameContaining(String name, Pageable pageable);
    
    Page<Product> findByCategoryAndNameContaining(String category, String name, Pageable pageable);
    
    Page<Product> findByOrganisationId(Long organisationId, Pageable pageable);
    List<Product> findByOrganisationId(Long organisationId);

    @Query("SELECT DISTINCT p.category FROM Product p")
    Set<String> findAllCategories();
    
    Page<Product> findByNameContainingAndCategoryAndActiveAndOrganisationId(
        String name, String category, boolean active, Long organisationId, Pageable pageable);
    
    Page<Product> findByNameContainingAndCategoryAndActive(
        String name, String category, boolean active, Pageable pageable);
    
    Page<Product> findByNameContainingAndCategoryAndOrganisationId(
        String name, String category, Long organisationId, Pageable pageable);
    
    Page<Product> findByNameContainingAndActiveAndOrganisationId(
        String name, boolean active, Long organisationId, Pageable pageable);
    
    Page<Product> findByNameContainingAndCategory(String name, String category, Pageable pageable);
    
    Page<Product> findByNameContainingAndActive(String name, boolean active, Pageable pageable);
    
    Page<Product> findByNameContainingAndOrganisationId(String name, Long organisationId, Pageable pageable);
    
    Page<Product> findByCategoryAndActiveAndOrganisationId(
        String category, boolean active, Long organisationId, Pageable pageable);
    
    Page<Product> findByCategoryAndActive(String category, boolean active, Pageable pageable);
    
    Page<Product> findByCategoryAndOrganisationId(String category, Long organisationId, Pageable pageable);
    
    Page<Product> findByActiveAndOrganisationId(boolean active, Long organisationId, Pageable pageable);
    
    Page<Product> findByActive(boolean active, Pageable pageable);

    @Query("SELECT COUNT(p) FROM Product p WHERE p.createdAt >= :date")
    long countByCreatedAtAfter(@Param("date") LocalDateTime date);

    @Query("SELECT COUNT(p) FROM Product p WHERE p.active = true")
    long countByActiveTrue();

    List<Product> findAllByOrganisationId(Long organisationId);

    @Query("SELECT COUNT(p) FROM Product p WHERE p.active = :active")
    long countByActive(@Param("active") boolean active);

    @Query(value = """
        SELECT DATE_TRUNC('month', created_at) as date, COUNT(*) as count 
        FROM products 
        GROUP BY DATE_TRUNC('month', created_at) 
        ORDER BY date DESC 
        LIMIT 12
        """, 
        nativeQuery = true)
    List<Object[]> countProductsByMonth();

    @Query(value = """
        SELECT p.name,
               COUNT(CASE WHEN s.status = 'ACTIVE' THEN 1 END) as active_subs,
               SUM(CASE WHEN MONTH(pay.created_at) = MONTH(CURRENT_DATE) THEN pay.amount ELSE 0 END) as monthly_income,
               AVG(DATEDIFF(s.end_date, s.start_date)) as avg_duration
        FROM products p
        LEFT JOIN subscription_plans sp ON sp.product_id = p.id
        LEFT JOIN subscriptions s ON s.plan_id = sp.id
        LEFT JOIN payments pay ON pay.subscription_id = s.id
        WHERE p.organisation_id IN (SELECT id FROM organisations WHERE owner_id = :userId)
        GROUP BY p.id
        """, nativeQuery = true)
    List<Object[]> getProductStatsByOrganisationOwnerId(Long userId);

    Page<Product> findByOrganisationOwnerId(Long userId, Pageable pageable);
    Page<Product> findByNameContainingAndOrganisationOwnerId(String name, Long userId, Pageable pageable);
    Page<Product> findByOrganisationIdAndOrganisationOwnerId(Long organisationId, Long userId, Pageable pageable);
    Page<Product> findByNameContainingAndOrganisationIdAndOrganisationOwnerId(
        String name, Long organisationId, Long userId, Pageable pageable);
    List<Product> findByOrganisationOwnerId(Long userId);

    @Query("SELECT COUNT(p) FROM Product p WHERE p.organisation.id = :organisationId")
    long countByOrganisationId(@Param("organisationId") Long organisationId);

    @Query("SELECT COUNT(p) FROM Product p WHERE p.organisation.owner.id = :ownerId")
    long countByOrganisationOwnerId(@Param("ownerId") Long ownerId);

    @Query("SELECT COUNT(p) FROM Product p WHERE p.organisation.id = :organisationId AND p.active = :active")
    long countByOrganisationIdAndActive(@Param("organisationId") Long organisationId, @Param("active") boolean active);

    Page<Product> findByNameContainingIgnoreCaseAndActive(String name, boolean active, Pageable pageable);

    Page<Product> findByActiveTrue(Pageable pageable);
    Page<Product> findByNameContainingIgnoreCaseAndActiveTrue(String name, Pageable pageable);
    
    @Query("SELECT p FROM Product p JOIN p.organisation o WHERE o.owner.id = :userId")
    List<Product> findByOrganiserId(@Param("userId") Long userId);

}