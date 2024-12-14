package com.example.service;

import com.example.dto.ProductStatisticsDto;
import com.example.dto.PlanStatisticsDto;
import com.example.dto.AdminStatisticsDto;
import com.example.dto.OrganiserStatisticsDto;
import com.example.dto.SubscriptionStatisticsDto;
import com.example.dto.stats.OrganiserStatisticsDetailedDto;
import com.example.entity.Subscription;
import com.example.enums.SubscriptionStatus;
import com.example.repository.PaymentRepository;
import com.example.repository.SubscriptionRepository;
import com.example.repository.UserRepository;
import com.example.repository.OrganisationRepository;
import com.example.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatisticsService {

    private final SubscriptionRepository subscriptionRepository;
    private final PaymentRepository paymentRepository;
    private final SubscriptionPlanService subscriptionPlanService;
    private final UserRepository userRepository;
    private final OrganisationRepository organisationRepository;
    private final ProductRepository productRepository;

    private Map<String, BigDecimal> convertRevenueGrowthData(List<Object[]> data) {
        Map<String, BigDecimal> result = new LinkedHashMap<>();
        for (Object[] row : data) {
            result.put(
                ((Timestamp) row[0]).toLocalDateTime().toString(),
                (BigDecimal) row[1]
            );
        }
        return result;
    }

    @Transactional(readOnly = true)
    public PlanStatisticsDto getPlanStatistics(Long planId) {
        PlanStatisticsDto stats = new PlanStatisticsDto();
        
        // Основные метрики
        stats.setActiveSubscriptions(subscriptionPlanService.countActiveSubscriptions(planId));
        stats.setTotalSubscriptions(subscriptionPlanService.countTotalSubscriptions(planId));
        stats.setAverageSubscriptionDays(subscriptionPlanService.getAverageSubscriptionDays(planId));
        stats.setRenewalRate(subscriptionPlanService.getRenewalRate(planId));
        
        // Доход за месяц
        LocalDateTime monthAgo = LocalDateTime.now().minusMonths(1);
        BigDecimal monthlyRevenue = paymentRepository.sumRevenueByPlanIdAndPeriod(planId, monthAgo);
        stats.setMonthlyRevenue(monthlyRevenue);
        
        // Конверсия (процент успешных платежей)
        long totalPayments = paymentRepository.countByPlanIdAndPeriod(planId, monthAgo);
        long successfulPayments = paymentRepository.countSuccessfulByPlanIdAndPeriod(planId, monthAgo);
        stats.setConversionRate(totalPayments > 0 ? (double) successfulPayments / totalPayments : 0);
        
        // Динамика подписчиков по дням
        Map<String, Integer> subscriberGrowth = new LinkedHashMap<>();
        subscriptionRepository.findSubscriberGrowthByPlanId(planId, monthAgo)
            .forEach(data -> subscriberGrowth.put(
                ((Timestamp) data[0]).toLocalDateTime().toString(),
                ((Number) data[1]).intValue()
            ));
        stats.setSubscriberGrowth(subscriberGrowth);
        
        // Динамика дохода по дням
        List<Object[]> revenueGrowthData = paymentRepository.findRevenueGrowthByPlanId(planId, monthAgo);
        stats.setRevenueGrowth(convertRevenueGrowthData(revenueGrowthData));
        
        return stats;
    }

    @Transactional(readOnly = true)
    public ProductStatisticsDto getProductStatistics(Long productId) {
        return ProductStatisticsDto.builder()
            .id(productId)
            .subscriptionPlansCount(subscriptionPlanService.countByProductId(productId))
            .activeSubscriptionPlansCount(subscriptionPlanService.countActiveByProductId(productId))
            .activeSubscriptionsCount(subscriptionRepository.countActiveByProductId(productId))
            .totalSubscriptionsCount(subscriptionRepository.countByProductId(productId))
            .monthlyIncome(paymentRepository.sumRevenueByProductIdAndPeriod(productId, LocalDateTime.now().minusMonths(1)).doubleValue())
            .totalIncome(paymentRepository.sumRevenueByProductId(productId).doubleValue())
            .build();
    }

    @Transactional(readOnly = true)
    public AdminStatisticsDto getAdminStatistics() {
        AdminStatisticsDto stats = new AdminStatisticsDto();
        LocalDateTime monthAgo = LocalDateTime.now().minusMonths(1);
        
        // Пользователи
        stats.setTotalUsers(userRepository.count());
        stats.setActiveUsers(userRepository.countByEnabled(true));
        stats.setNewUsersThisMonth(userRepository.countByCreatedAtAfter(monthAgo));
        
        // Организации
        stats.setTotalOrganisations(organisationRepository.count());
        stats.setActiveOrganisations(organisationRepository.countByActive(true));
        stats.setNewOrganisationsThisMonth(organisationRepository.countByCreatedAtAfter(monthAgo));
        
        // Продукты
        stats.setTotalProducts(productRepository.count());
        stats.setActiveProducts(productRepository.countByActive(true));
        stats.setNewProductsThisMonth(productRepository.countByCreatedAtAfter(monthAgo));
        
        // Подписки
        stats.setTotalSubscriptions(subscriptionRepository.count());
        stats.setActiveSubscriptions(subscriptionRepository.countByActive(true));
        stats.setNewSubscriptionsThisMonth(subscriptionRepository.countByCreatedAtAfter(monthAgo));
        
        // Финансы
        stats.setTotalRevenue(paymentRepository.sumAllPayments());
        stats.setMonthlyRevenue(paymentRepository.sumPaymentsAfter(monthAgo));
        
        // Графики роста
        Map<String, Integer> userGrowth = new LinkedHashMap<>();
        userRepository.countUsersByMonth()
            .forEach(data -> userGrowth.put(
                ((Timestamp) data[0]).toLocalDateTime().toString(),
                ((Number) data[1]).intValue()
            ));
        stats.setUserGrowth(userGrowth);
        
        Map<String, Integer> organisationGrowth = new LinkedHashMap<>();
        organisationRepository.countOrganisationsByMonth()
            .forEach(data -> organisationGrowth.put(
                ((Timestamp) data[0]).toLocalDateTime().toString(),
                ((Number) data[1]).intValue()
            ));
        stats.setOrganisationGrowth(organisationGrowth);
        
        Map<String, Integer> productGrowth = new LinkedHashMap<>();
        productRepository.countProductsByMonth()
            .forEach(data -> productGrowth.put(
                ((Timestamp) data[0]).toLocalDateTime().toString(),
                ((Number) data[1]).intValue()
            ));
        stats.setProductGrowth(productGrowth);
        
        Map<String, BigDecimal> revenueGrowth = new LinkedHashMap<>();
        paymentRepository.sumPaymentsByMonth()
            .forEach(data -> revenueGrowth.put(
                ((Timestamp) data[0]).toLocalDateTime().toString(),
                (BigDecimal) data[1]
            ));
        stats.setRevenueGrowth(revenueGrowth);
        
        Map<String, Integer> subscriptionGrowth = new LinkedHashMap<>();
        subscriptionRepository.countSubscriptionsByMonth()
            .forEach(data -> subscriptionGrowth.put(
                ((Timestamp) data[0]).toLocalDateTime().toString(),
                ((Number) data[1]).intValue()
            ));
        stats.setSubscriptionGrowth(subscriptionGrowth);
        
        return stats;
    }

    @Transactional(readOnly = true)
    public OrganiserStatisticsDetailedDto getOrganiserStatistics(Long userId) {
        // Базовые метрики
            long organisationsCount = organisationRepository.countByOwnerId(userId);
        long productsCount = productRepository.countByOrganisationOwnerId(userId);
        long activeSubscriptionsCount = subscriptionRepository.countActiveByOrganisationOwnerId(userId);
        long totalSubscriptionsCount = subscriptionRepository.countByOrganisationOwnerId(userId);
        long completedSubscriptionsCount = subscriptionRepository.countByOrganisationOwnerIdAndStatus(
            userId, 
            SubscriptionStatus.EXPIRED
        );
        long canceledSubscriptionsCount = subscriptionRepository.countByOrganisationOwnerIdAndStatus(
            userId, 
            SubscriptionStatus.CANCELLED
        );

        // Финансовые метрики
        LocalDateTime monthStart = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0);
        double monthlyIncome = paymentRepository.sumByOrganisationIdAndCreatedAtAfter(userId, monthStart).doubleValue();
        double totalIncome = paymentRepository.sumByOrganisationOwnerId(userId).doubleValue();

        // Метрики эффективности
        double averageSubscriptionDuration = subscriptionRepository.getAverageSubscriptionDurationByOrganisationOwnerId(userId);
        double renewalRate = calculateRenewalRate(userId);
        int newSubscribersThisMonth = subscriptionRepository.countNewByOrganisationOwnerIdAndCreatedAtAfter(userId, monthStart);
        int totalSubscribers = subscriptionRepository.countDistinctUsersByOrganisationOwnerId(userId);

        // Данные для графиков
        List<OrganiserStatisticsDetailedDto.MonthlySubscriptionStats> monthlyStats = getMonthlyStats(userId);
        List<OrganiserStatisticsDetailedDto.ProductStats> productStats = getProductStats(userId);

        return OrganiserStatisticsDetailedDto.builder()
                .organisationsCount(organisationsCount)
                .productsCount(productsCount)
                .activeSubscriptionsCount(activeSubscriptionsCount)
                .totalSubscriptionsCount(totalSubscriptionsCount)
                .completedSubscriptionsCount(completedSubscriptionsCount)
                .canceledSubscriptionsCount(canceledSubscriptionsCount)
                .monthlyIncome(monthlyIncome)
                .totalIncome(totalIncome)
                .averageSubscriptionDuration(averageSubscriptionDuration)
                .renewalRate(renewalRate)
                .newSubscribersThisMonth(newSubscribersThisMonth)
                .totalSubscribers(totalSubscribers)
                .monthlyStats(monthlyStats)
                .productStats(productStats)
                .build();
    }

    private List<OrganiserStatisticsDetailedDto.MonthlySubscriptionStats> getMonthlyStats(Long userId) {
        LocalDateTime sixMonthsAgo = LocalDateTime.now().minusMonths(6).withDayOfMonth(1).withHour(0).withMinute(0);
        List<Object[]> monthlyData = subscriptionRepository.getMonthlyStatsByOrganisationOwnerId(userId, sixMonthsAgo);
        
        return monthlyData.stream()
                .map(data -> OrganiserStatisticsDetailedDto.MonthlySubscriptionStats.builder()
                        .month((String) data[0])
                        .newSubscriptions((Integer) data[1])
                        .renewals((Integer) data[2])
                        .cancellations((Integer) data[3])
                        .income((Double) data[4])
                        .build())
                .collect(Collectors.toList());
    }

    private List<OrganiserStatisticsDetailedDto.ProductStats> getProductStats(Long userId) {
        List<Object[]> productData = productRepository.getProductStatsByOrganisationOwnerId(userId);
        
        return productData.stream()
                .map(data -> OrganiserStatisticsDetailedDto.ProductStats.builder()
                        .productName((String) data[0])
                        .activeSubscriptions((Long) data[1])
                        .monthlyIncome((Double) data[2])
                        .averageSubscriptionDuration((Double) data[3])
                        .build())
                .collect(Collectors.toList());
    }

    private double calculateRenewalRate(Long userId) {
        long totalRenewals = subscriptionRepository.countRenewalsByOrganisationOwnerId(userId);
        long totalExpired = subscriptionRepository.countExpiredByOrganisationOwnerId(userId);
        return totalExpired > 0 ? (double) totalRenewals / totalExpired : 0;
    }

    @Transactional(readOnly = true)
    public SubscriptionStatisticsDto getSubscriptionStatistics(Long subscriptionId) {
        SubscriptionStatisticsDto stats = new SubscriptionStatisticsDto();
        LocalDateTime monthAgo = LocalDateTime.now().minusMonths(1);
        
        Subscription subscription = subscriptionRepository.findById(subscriptionId)
            .orElseThrow(() -> new RuntimeException("Subscription not found"));
        
        // Основные метрики
        stats.setStartDate(subscription.getStartDate());
        stats.setEndDate(subscription.getEndDate());
        stats.setStatus(subscription.getStatus());
        stats.setActive(subscription.isActive());
        
        // Платежи
        BigDecimal totalPaid = paymentRepository.sumBySubscriptionId(subscriptionId);
        stats.setTotalPaid(totalPaid);
        
        long totalPayments = paymentRepository.countBySubscriptionId(subscriptionId);
        long successfulPayments = paymentRepository.countSuccessfulBySubscriptionId(subscriptionId);
        stats.setPaymentSuccessRate(totalPayments > 0 ? (double) successfulPayments / totalPayments : 0);
        
        // История платежей по месяцам
        Map<String, BigDecimal> paymentHistory = new LinkedHashMap<>();
        paymentRepository.findPaymentHistoryBySubscriptionId(subscriptionId)
            .forEach(data -> paymentHistory.put(
                ((LocalDateTime) data[0]).toString(),
                (BigDecimal) data[1]
            ));
        stats.setPaymentHistory(paymentHistory);
        
        return stats;
    }

    public OrganiserStatisticsDetailedDto getOrganisationStatistics(Long organisationId) {
        return OrganiserStatisticsDetailedDto.builder()
            .productsCount(productRepository.countByOrganisationId(organisationId))
            .activeSubscriptionsCount(subscriptionRepository.countByOrganisationIdAndActive(organisationId))
            .totalSubscriptionsCount(subscriptionRepository.countByOrganisationId(organisationId))
            .monthlyIncome(paymentRepository.sumByOrganisationIdAndCreatedAtAfter(
                organisationId, LocalDateTime.now().withDayOfMonth(1)).doubleValue())
            .totalIncome(paymentRepository.sumByOrganisationId(organisationId).doubleValue())
            .build();
    }
} 