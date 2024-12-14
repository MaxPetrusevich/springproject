package com.example.repository;

import com.example.entity.Payment;
import com.example.dto.RevenueGrowthData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findBySubscriptionId(Long subscriptionId);
    
    List<Payment> findByUserId(Long userId);
    
    @Query("SELECT p FROM Payment p WHERE p.user.id = :userId ORDER BY p.paymentDate DESC")
    List<Payment> findTop5ByUserIdOrderByPaymentDateDesc(@Param("userId") Long userId);
    
    @Query("SELECT SUM(p.amount) FROM Payment p " +
           "WHERE p.subscription.plan.id = :planId " +
           "AND p.createdAt >= :since " +
           "AND p.status = 'COMPLETED'")
    BigDecimal sumRevenueByPlanIdAndPeriod(@Param("planId") Long planId, @Param("since") LocalDateTime since);
    
    @Query("SELECT COUNT(p) FROM Payment p " +
           "WHERE p.subscription.plan.id = :planId " +
           "AND p.createdAt >= :since")
    long countByPlanIdAndPeriod(@Param("planId") Long planId, @Param("since") LocalDateTime since);
    
    @Query("SELECT COUNT(p) FROM Payment p " +
           "WHERE p.subscription.plan.id = :planId " +
           "AND p.createdAt >= :since " +
           "AND p.status = 'COMPLETED'")
    long countSuccessfulByPlanIdAndPeriod(@Param("planId") Long planId, @Param("since") LocalDateTime since);
    
    @Query(value = """
        SELECT DATE_TRUNC('day', p.created_at) as date, SUM(p.amount) as amount 
        FROM payments p 
        JOIN subscriptions s ON p.subscription_id = s.id 
        JOIN subscription_plans sp ON s.plan_id = sp.id 
        WHERE sp.id = :planId 
        AND p.created_at >= :since 
        AND p.status = 'COMPLETED' 
        GROUP BY DATE_TRUNC('day', p.created_at) 
        ORDER BY date
        """, 
        nativeQuery = true)
    List<Object[]> findRevenueGrowthByPlanId(
        @Param("planId") Long planId, 
        @Param("since") LocalDateTime since
    );
    
    @Query("SELECT SUM(p.amount) FROM Payment p " +
           "WHERE p.subscription.plan.product.id = :productId " +
           "AND p.createdAt >= :since " +
           "AND p.status = 'COMPLETED'")
    BigDecimal sumRevenueByProductIdAndPeriod(@Param("productId") Long productId, @Param("since") LocalDateTime since);
    
    @Query("SELECT COUNT(p) FROM Payment p " +
           "WHERE p.subscription.plan.product.id = :productId " +
           "AND p.createdAt >= :since")
    long countByProductIdAndPeriod(@Param("productId") Long productId, @Param("since") LocalDateTime since);
    
    @Query("SELECT COUNT(p) FROM Payment p " +
           "WHERE p.subscription.plan.product.id = :productId " +
           "AND p.createdAt >= :since " +
           "AND p.status = 'COMPLETED'")
    long countSuccessfulByProductIdAndPeriod(@Param("productId") Long productId, @Param("since") LocalDateTime since);
    
    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.status = 'COMPLETED'")
    BigDecimal sumAllPayments();
    
    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.status = 'COMPLETED' AND p.paymentDate >= :date")
    BigDecimal sumPaymentsAfter(@Param("date") LocalDateTime date);
    
    @Query(value = """
        SELECT DATE_TRUNC('month', payment_date) as date, SUM(amount) as sum 
        FROM payments 
        WHERE status = 'COMPLETED'
        GROUP BY DATE_TRUNC('month', payment_date) 
        ORDER BY date DESC 
        LIMIT 12
        """, 
        nativeQuery = true)
    List<Object[]> sumPaymentsByMonth();
    
    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p " +
           "WHERE p.subscription.plan.product.organisation.id = :organiserId " +
           "AND p.status = 'COMPLETED'")
    BigDecimal sumRevenueByOrganiserId(@Param("organiserId") Long organiserId);
    
    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p " +
           "WHERE p.subscription.plan.product.organisation.id = :organiserId " +
           "AND p.createdAt >= :since " +
           "AND p.status = 'COMPLETED'")
    BigDecimal sumRevenueByOrganiserIdAndPeriod(@Param("organiserId") Long organiserId, @Param("since") LocalDateTime since);
    
    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.subscription.id = :subscriptionId")
    BigDecimal sumBySubscriptionId(@Param("subscriptionId") Long subscriptionId);
    
    @Query("SELECT COUNT(p) FROM Payment p WHERE p.subscription.id = :subscriptionId")
    long countBySubscriptionId(@Param("subscriptionId") Long subscriptionId);
    
    @Query("SELECT COUNT(p) FROM Payment p WHERE p.subscription.id = :subscriptionId AND p.status = 'COMPLETED'")
    long countSuccessfulBySubscriptionId(@Param("subscriptionId") Long subscriptionId);
    
    @Query("SELECT p.paymentDate as date, p.amount as amount FROM Payment p " +
           "WHERE p.subscription.id = :subscriptionId AND p.status = 'COMPLETED' " +
           "ORDER BY date DESC")
    List<Object[]> findPaymentHistoryBySubscriptionId(@Param("subscriptionId") Long subscriptionId);
    
    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.subscription.plan.product.organisation.id = :organisationId")
    BigDecimal sumByOrganisationId(@Param("organisationId") Long organisationId);
    
    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.subscription.plan.product.organisation.id = :organisationId AND p.createdAt >= :date")
    BigDecimal sumByOrganisationIdAndCreatedAtAfter(@Param("organisationId") Long organisationId, @Param("date") LocalDateTime date);
    
    List<Payment> findBySubscriptionIdOrderByCreatedAtDesc(Long subscriptionId);
    
    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p " +
           "WHERE p.subscription.plan.product.organisation.owner.id = :ownerId " +
           "AND p.status = 'COMPLETED'")
    BigDecimal sumByOrganisationOwnerId(@Param("ownerId") Long ownerId);
    
    @Query(value = """
        SELECT COALESCE(SUM(p.amount), 0)
        FROM payments p
        JOIN subscriptions s ON s.id = p.subscription_id
        JOIN subscription_plans sp ON sp.id = s.plan_id
        WHERE sp.product_id = :productId
        AND DATE_TRUNC('month', p.created_at) = DATE_TRUNC('month', CURRENT_DATE)
        """, nativeQuery = true)
    BigDecimal getMonthlyRevenueByProductId(@Param("productId") Long productId);
    
    @Query(value = """
        SELECT COALESCE(SUM(p.amount), 0)
        FROM payments p
        JOIN subscriptions s ON s.id = p.subscription_id
        JOIN subscription_plans sp ON sp.id = s.plan_id
        WHERE sp.product_id = :productId
        """, nativeQuery = true)
    BigDecimal getTotalRevenueByProductId(@Param("productId") Long productId);
    
    @Query(value = """
        SELECT COALESCE(SUM(p.amount), 0)
        FROM payments p
        JOIN subscriptions s ON s.id = p.subscription_id
        WHERE s.plan_id = :planId
        AND DATE_TRUNC('month', p.created_at) = DATE_TRUNC('month', CURRENT_DATE)
        AND p.status = 'COMPLETED'
        """, nativeQuery = true)
    BigDecimal getMonthlyRevenueByPlanId(@Param("planId") Long planId);
    
    @Query(value = """
        SELECT COALESCE(SUM(p.amount), 0)
        FROM payments p
        JOIN subscriptions s ON s.id = p.subscription_id
        WHERE s.plan_id = :planId
        AND p.status = 'COMPLETED'
        """, nativeQuery = true)
    BigDecimal getTotalRevenueByPlanId(@Param("planId") Long planId);
    

    @Query("""
        SELECT COALESCE(SUM(p.amount), 0)
        FROM Payment p
        WHERE p.subscription.plan.product.organisation.owner.id = :ownerId
        AND p.createdAt >= :date
        """)
    BigDecimal sumByOrganisationOwnerIdAndCreatedAtAfter(
        @Param("ownerId") Long ownerId,
        @Param("date") LocalDateTime date
    );
    
    @Query(value = """
        SELECT COALESCE(SUM(p.amount), 0)
        FROM payments p
        JOIN subscriptions s ON s.id = p.subscription_id
        JOIN subscription_plans sp ON sp.id = s.plan_id
        WHERE sp.product_id = :productId
        AND p.status = 'COMPLETED'
        """, nativeQuery = true)
    BigDecimal sumRevenueByProductId(@Param("productId") Long productId);
    

} 