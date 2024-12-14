package com.example.repository;

import com.example.entity.Subscription;
import com.example.entity.SubscriptionPlan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SubscriptionPlanRepository extends JpaRepository<SubscriptionPlan, Long> {
    @Query("SELECT sp FROM SubscriptionPlan sp WHERE sp.product.id = :productId")
    List<SubscriptionPlan> findByProductId(@Param("productId") Long productId);
    
    Page<SubscriptionPlan> findByProductId(Long productId, Pageable pageable);
    
    List<SubscriptionPlan> findByProductOrganisationId(Long organiserId);
    
    @Query("SELECT sp FROM SubscriptionPlan sp " +
           "LEFT JOIN FETCH sp.product p " +
           "LEFT JOIN FETCH p.organisation o " +
           "WHERE o.id = :organisationId")
    List<SubscriptionPlan> findByProductOrganisationIdWithSubscriptions(@Param("organisationId") Long organisationId);
    
    @Query("SELECT s FROM Subscription s WHERE s.plan.id = :planId AND s.active = true")
    List<SubscriptionPlan> findActiveByPlanId(@Param("planId") Long planId);
    
    @Query("SELECT COUNT(s) FROM Subscription s WHERE s.plan.id = :planId AND s.active = true")
    long countActiveByPlanId(@Param("planId") Long planId);
    
    @Query("SELECT COUNT(s) FROM Subscription s WHERE s.plan.id = :planId")
    long countByPlanId(@Param("planId") Long planId);

    
    @Query("SELECT COUNT(sp) FROM SubscriptionPlan sp WHERE sp.product.id = :productId AND sp.active = true")
    long countActiveByProductId(@Param("productId") Long productId);
    
    @Query("SELECT COUNT(sp) FROM SubscriptionPlan sp WHERE sp.product.id = :productId")
    long countByProductId(@Param("productId") Long productId);
    
    @Query("SELECT s FROM Subscription s " +
           "WHERE s.plan.product.organisation.id = :organiserId")
    List<Subscription> findByPlanProductOrganisationId(@Param("organiserId") Long organiserId);
    
    @Query("SELECT COUNT(s) FROM Subscription s " +
           "WHERE s.plan.id = :planId AND s.active = true")
    long countActiveSubscriptions(@Param("planId") Long planId);
    
    @Query("SELECT COUNT(s) FROM Subscription s WHERE s.plan.id = :planId")
    long countTotalSubscriptions(@Param("planId") Long planId);
    
    @Query(value = """
        SELECT COALESCE(AVG(
            EXTRACT(EPOCH FROM (COALESCE(s.end_date, CURRENT_TIMESTAMP) - s.start_date))/86400
        ), 0)
        FROM subscriptions s 
        WHERE s.plan_id = :planId
        """, 
        nativeQuery = true)
    double getAverageSubscriptionDays(@Param("planId") Long planId);
    
    @Query(value = """
        SELECT CAST(COUNT(CASE WHEN s.renewed_at IS NOT NULL THEN 1 END) AS FLOAT) * 100 / 
        NULLIF(COUNT(*), 0)
        FROM subscriptions s 
        WHERE s.plan_id = :planId
        """, 
        nativeQuery = true)
    double getRenewalRate(@Param("planId") Long planId);
    
    @Query("SELECT COUNT(s) FROM Subscription s " +
           "WHERE s.plan.id = :planId AND s.createdAt >= :since")
    long countNewSubscriptions(@Param("planId") Long planId, @Param("since") LocalDateTime since);
    
    @Query("SELECT COUNT(s) FROM Subscription s " +
           "WHERE s.plan.id = :planId AND s.active = true AND s.createdAt >= :since")
    long countActiveSubscriptionsSince(@Param("planId") Long planId, @Param("since") LocalDateTime since);
    
    List<SubscriptionPlan> findByIdAndActive(Long id, boolean active);
    
    Page<SubscriptionPlan> findByProductOrganisationOwnerId(Long userId, Pageable pageable);
    Page<SubscriptionPlan> findByNameContainingAndProductOrganisationOwnerId(String name, Long userId, Pageable pageable);
    Page<SubscriptionPlan> findByProductIdAndProductOrganisationOwnerId(Long productId, Long userId, Pageable pageable);
    Page<SubscriptionPlan> findByNameContainingAndProductIdAndProductOrganisationOwnerId(
        String name, Long productId, Long userId, Pageable pageable);
    
    Page<SubscriptionPlan> findByNameContaining(String name, Pageable pageable);
    
    Page<SubscriptionPlan> findByNameContainingAndProductId(String name, Long productId, Pageable pageable);

    @Query("SELECT COUNT(sp) FROM SubscriptionPlan sp WHERE sp.product.organisation.id = :organisationId")
    long countByProductOrganisationId(@Param("organisationId") Long organisationId);

    @Query("SELECT COUNT(sp) FROM SubscriptionPlan sp WHERE sp.product.organisation.id = :organisationId AND sp.active = :active")
    long countByProductOrganisationIdAndActive(@Param("organisationId") Long organisationId, @Param("active") boolean active);

    @Query("SELECT COUNT(sp) FROM SubscriptionPlan sp WHERE sp.product.id = :productId AND sp.active = :active")
    long countByProduct_IdAndActive(@Param("productId") Long productId, @Param("active") boolean active);
} 