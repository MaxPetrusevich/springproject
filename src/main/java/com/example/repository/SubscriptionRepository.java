package com.example.repository;

import com.example.entity.Subscription;
import com.example.entity.SubscriptionPlan;
import com.example.entity.User;
import com.example.enums.SubscriptionStatus;
import com.example.dto.SubscriberGrowthData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    List<Subscription> findByUser(User user);

    @Query("SELECT COUNT(s) FROM Subscription s WHERE s.status = 'ACTIVE'")
    long countByStatusActive();

    @Query("SELECT COUNT(s) FROM Subscription s WHERE s.createdAt >= :date")
    long countByCreatedAtAfter(LocalDateTime date);

    @Query(value = """
            SELECT DATE_TRUNC('month', created_at) as date, COUNT(*) as count 
            FROM subscriptions 
            GROUP BY DATE_TRUNC('month', created_at) 
            ORDER BY date DESC 
            LIMIT 12
            """,
            nativeQuery = true)
    List<Object[]> countSubscriptionsByMonth();

    List<Subscription> findTop5ByOrderByCreatedAtDesc();

    @Query("SELECT COUNT(s) FROM Subscription s " +
            "WHERE s.plan.product.id = :productId " +
            "AND s.active = true")
    long countActiveByProductId(@Param("productId") Long productId);

    @Query("SELECT COUNT(s) FROM Subscription s " +
            "WHERE s.plan.product.id = :productId")
    long countByProductId(@Param("productId") Long productId);

    @Query(value = """
            SELECT DATE_TRUNC('day', created_at) as date, COUNT(*) as count 
            FROM subscriptions 
            WHERE plan_id = :planId 
            AND created_at >= :since 
            GROUP BY DATE_TRUNC('day', created_at) 
            ORDER BY date
            """,
            nativeQuery = true)
    List<Object[]> findSubscriberGrowthByPlanId(
            @Param("planId") Long planId,
            @Param("since") LocalDateTime since
    );

    @Query("SELECT s FROM Subscription s WHERE " +
            "(LOWER(s.user.email) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(s.plan.name) LIKE LOWER(CONCAT('%', :search, '%'))) " +
            "AND s.status = :status")
    Page<Subscription> findBySearchAndStatus(String search, SubscriptionStatus status, Pageable pageable);

    @Query("SELECT s FROM Subscription s WHERE " +
            "LOWER(s.user.email) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(s.plan.name) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<Subscription> findBySearch(String search, Pageable pageable);

    Page<Subscription> findByStatus(SubscriptionStatus status, Pageable pageable);

    Page<Subscription> findByUserId(Long userId, Pageable pageable);

    Page<Subscription> findByPlanId(Long planId, Pageable pageable);

    boolean existsByUserAndPlanAndActive(User user, SubscriptionPlan plan, boolean active);

    List<Subscription> findByActive(boolean active);

    long countByActive(boolean active);

    @Query("SELECT COUNT(s) FROM Subscription s " +
            "WHERE s.plan.product.organisation.id = :organiserId " +
            "AND s.active = :active")
    long countByOrganiserIdAndActive(@Param("organiserId") Long organiserId, @Param("active") boolean active);

    @Query("SELECT COUNT(s) FROM Subscription s " +
            "WHERE s.plan.product.organisation.id = :organiserId")
    long countByOrganiserId(@Param("organiserId") Long organiserId);

    List<Subscription> findByUserId(Long userId);

    List<Subscription> findByPlanProductOrganisationId(Long organiserId);

    @Query(value = "SELECT CAST(COUNT(CASE WHEN s.renewed_at IS NOT NULL THEN 1 END) AS FLOAT) * 100 / " +
            "NULLIF(COUNT(*), 0) " +
            "FROM subscriptions s " +
            "WHERE s.plan_id = :planId", nativeQuery = true)
    double getRenewalRate(@Param("planId") Long planId);

    List<Subscription> findByStatusAndEndDateBefore(SubscriptionStatus status, LocalDateTime date);

    List<Subscription> findByStatusAndEndDateBetween(
            SubscriptionStatus status,
            LocalDateTime startDate,
            LocalDateTime endDate
    );

    @Query("SELECT COUNT(s) FROM Subscription s WHERE s.plan.product.organisation.id = :organisationId")
    long countByOrganisationId(@Param("organisationId") Long organisationId);

    @Query("SELECT COUNT(s) FROM Subscription s WHERE s.plan.product.organisation.id = :organisationId AND s.active = true")
    long countByOrganisationIdAndActive(@Param("organisationId") Long organisationId);


    @Query("SELECT COUNT(DISTINCT s.user.id) FROM Subscription s WHERE s.plan.product.organisation.owner.id = :userId")
    int countDistinctUsersByOrganisationOwnerId(Long userId);

    @Query(value = """
            SELECT COALESCE(AVG(EXTRACT(EPOCH FROM (s.end_date - s.start_date))/86400), 0)
            FROM subscriptions s
            JOIN subscription_plans sp ON s.plan_id = sp.id
            JOIN products p ON sp.product_id = p.id
            JOIN organisations o ON p.organisation_id = o.id
            WHERE o.owner_id = :userId
            """, nativeQuery = true)
    double getAverageSubscriptionDurationByOrganisationOwnerId(@Param("userId") Long userId);

    @Query(value = """
            SELECT TO_CHAR(s.created_at, 'YYYY-MM') as month,
                   COUNT(CASE WHEN s.is_renewal = false THEN 1 END) as new_subs,
                   COUNT(CASE WHEN s.is_renewal = true THEN 1 END) as renewals,
                   COUNT(CASE WHEN s.status = 'CANCELLED' THEN 1 END) as cancellations,
                   COALESCE(SUM(p.amount), 0) as income
            FROM subscriptions s
            LEFT JOIN payments p ON p.subscription_id = s.id
            WHERE s.plan_id IN (
                SELECT id FROM subscription_plans WHERE product_id IN (
                    SELECT id FROM products WHERE organisation_id IN (
                        SELECT id FROM organisations WHERE owner_id = :userId
                    )
                )
            )
            AND s.created_at >= :startDate
            GROUP BY TO_CHAR(s.created_at, 'YYYY-MM')
            ORDER BY month DESC
            """, nativeQuery = true)
    List<Object[]> getMonthlyStatsByOrganisationOwnerId(Long userId, LocalDateTime startDate);


    @Query("SELECT COUNT(s) FROM Subscription s WHERE s.plan.product.organisation.owner.id = :userId AND s.endDate < CURRENT_TIMESTAMP")
    long countExpiredByOrganisationOwnerId(@Param("userId") Long userId);

    @Query("SELECT COUNT(s) FROM Subscription s WHERE s.plan.product.organisation.owner.id = :userId AND s.createdAt >= :date")
    int countNewByOrganisationOwnerIdAndCreatedAtAfter(@Param("userId") Long userId, @Param("date") LocalDateTime date);

    @Query("SELECT COUNT(s) FROM Subscription s " +
            "WHERE s.plan.product.organisation.owner.id = :ownerId " +
            "AND s.status = :status")
    long countByOrganisationOwnerIdAndStatus(
            @Param("ownerId") Long ownerId,
            @Param("status") SubscriptionStatus status
    );

    @Query("SELECT COUNT(s) FROM Subscription s WHERE s.plan.product.id = :productId AND s.active = :active")
    long countByProductIdAndActive(@Param("productId") Long productId, @Param("active") boolean active);

    @Query("SELECT COUNT(s) FROM Subscription s WHERE s.plan.id = :planId AND s.active = :active")
    long countByPlanIdAndActive(@Param("planId") Long planId, @Param("active") boolean active);

    @Query("SELECT COUNT(s) FROM Subscription s WHERE s.plan.id = :planId")
    long countByPlanId(@Param("planId") Long planId);

    @Query("SELECT COUNT(s) FROM Subscription s WHERE s.plan.id = :planId AND s.renewedAt IS NOT NULL")
    long countRenewalsByPlanId(@Param("planId") Long planId);

    @Query("SELECT COUNT(s) FROM Subscription s WHERE s.plan.id = :planId AND s.createdAt >= :date")
    long countByPlanIdAndCreatedAtAfter(@Param("planId") Long planId, @Param("date") LocalDateTime date);

    @Query("SELECT COUNT(s) FROM Subscription s " +
            "WHERE s.plan.product.organisation.owner.id = :ownerId " +
            "AND s.active = true")
    long countActiveByOrganisationOwnerId(@Param("ownerId") Long ownerId);

    @Query("SELECT COUNT(s) FROM Subscription s " +
            "WHERE s.plan.product.organisation.owner.id = :ownerId")
    long countByOrganisationOwnerId(@Param("ownerId") Long ownerId);

    @Query("SELECT COUNT(s) FROM Subscription s " +
            "WHERE s.plan.product.organisation.owner.id = :ownerId " +
            "AND s.renewedAt IS NOT NULL")
    long countRenewalsByOrganisationOwnerId(@Param("ownerId") Long ownerId);

    Page<Subscription> findByUserIdAndStatus(Long userId, SubscriptionStatus status, Pageable pageable);

    List<Subscription> findByUserEmail(String email);

    List<Subscription> findByUserEmailAndStatus(String email, SubscriptionStatus status);

    @Query(value = """
            SELECT p.name,
                   COUNT(CASE WHEN s.status = 'ACTIVE' THEN 1 END) as active_subs,
                   COALESCE(SUM(CASE 
                       WHEN DATE_TRUNC('month', pay.created_at) = DATE_TRUNC('month', CURRENT_DATE) 
                       THEN pay.amount 
                       ELSE 0 
                   END), 0) as monthly_income,
                   COALESCE(AVG(EXTRACT(EPOCH FROM (s.end_date - s.start_date))/86400), 0) as avg_duration
            FROM products p
            LEFT JOIN subscription_plans sp ON sp.product_id = p.id
            LEFT JOIN subscriptions s ON s.plan_id = sp.id
            LEFT JOIN payments pay ON pay.subscription_id = s.id
            WHERE p.organisation_id IN (SELECT id FROM organisations WHERE owner_id = :userId)
            GROUP BY p.id, p.name
            """, nativeQuery = true)
    List<Object[]> getProductStatsByOrganisationOwnerId(Long userId);

    @Query("SELECT s FROM Subscription s WHERE s.plan.product.id = :productId")
    List<Subscription> findByPlanProductId(@Param("productId") Long productId);

    @Query("SELECT COUNT(s) FROM Subscription s WHERE s.plan.product.id = :productId")
    long countByPlanProductId(@Param("productId") Long productId);

    @Query("SELECT COUNT(s) FROM Subscription s " +
            "WHERE s.plan.product.id = :productId " +
            "AND s.renewedAt IS NOT NULL")
    long countByPlanProductIdAndRenewedAtIsNotNull(@Param("productId") Long productId);

    @Query("SELECT COUNT(s) FROM Subscription s WHERE s.plan.id = :planId AND s.active = true")
    long countActiveByPlanId(@Param("planId") Long planId);

    @Query("SELECT COUNT(s) FROM Subscription s " +
            "WHERE s.plan.id = :planId " +
            "AND s.renewedAt IS NOT NULL")
    long countByPlanIdAndRenewedAtIsNotNull(@Param("planId") Long planId);

    @Query("SELECT COUNT(s) FROM Subscription s " +
            "WHERE s.plan.id = :planId AND s.status = :status")
    long countByPlanIdAndStatus(@Param("planId") Long planId, @Param("status") SubscriptionStatus status);

    @Query("SELECT AVG(EXTRACT(EPOCH FROM (s.endDate - s.startDate))/86400) " +
            "FROM Subscription s WHERE s.plan.id = :planId")
    Double getAverageSubscriptionDuration(@Param("planId") Long planId);

    @Query("SELECT COUNT(s) FROM Subscription s " +
            "WHERE s.plan.id = :planId AND s.createdAt >= :startDate")
    int countNewSubscribersByPlanId(@Param("planId") Long planId, @Param("startDate") LocalDateTime startDate);
}