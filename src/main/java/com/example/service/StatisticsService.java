package com.example.service;

import com.example.dto.ProductStatisticsDto;
import com.example.dto.PlanStatisticsDto;
import com.example.dto.AdminStatisticsDto;
import com.example.dto.OrganiserStatisticsDto;
import com.example.dto.SubscriptionStatisticsDto;
import com.example.dto.stats.OrganiserStatisticsDetailedDto;
import com.example.dto.stats.SubscriptionPlanStatisticsDto;
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
    public SubscriptionPlanStatisticsDto getPlanStatistics(Long planId) {
        try {
            SubscriptionPlanStatisticsDto stats = new SubscriptionPlanStatisticsDto();
            LocalDateTime monthAgo = LocalDateTime.now().minusMonths(1);

            // Подписки
            stats.setActiveSubscriptions(subscriptionRepository.countActiveByPlanId(planId));
            stats.setTotalSubscriptions(subscriptionRepository.countByPlanId(planId));
            stats.setActiveSubscriptionsCount(stats.getActiveSubscriptions());
            stats.setTotalSubscriptionsCount(stats.getTotalSubscriptions());
            stats.setCompletedSubscriptionsCount(subscriptionRepository.countByPlanIdAndStatus(planId, SubscriptionStatus.EXPIRED));
            stats.setCanceledSubscriptionsCount(subscriptionRepository.countByPlanIdAndStatus(planId, SubscriptionStatus.CANCELLED));
            
            // Доходы
            BigDecimal monthlyRevenue = paymentRepository.getMonthlyRevenueByPlanId(planId);
            stats.setMonthlyRevenue(monthlyRevenue != null ? monthlyRevenue : BigDecimal.ZERO);
            stats.setMonthlyIncome(stats.getMonthlyRevenue().doubleValue());
            
            BigDecimal totalRevenue = paymentRepository.getTotalRevenueByPlanId(planId);
            stats.setTotalRevenue(totalRevenue != null ? totalRevenue : BigDecimal.ZERO);
            stats.setTotalIncome(stats.getTotalRevenue().doubleValue());

            // Средняя продолжительность подписки
            Double avgDuration = subscriptionRepository.getAverageSubscriptionDuration(planId);
            stats.setAverageSubscriptionDuration(avgDuration != null ? avgDuration : 0.0);

            // Новые подписчики
            stats.setNewSubscribersThisMonth(subscriptionRepository.countNewSubscribersByPlanId(planId, monthAgo));
            stats.setTotalSubscribers((int) stats.getTotalSubscriptions());

            // Коэффициент конверсии
            long totalPayments = paymentRepository.countBySubscriptionPlanId(planId);
            long successfulPayments = paymentRepository.countSuccessfulBySubscriptionPlanId(planId);
            double conversionRate = totalPayments > 0 ? (double) successfulPayments / totalPayments : 0;
            stats.setConversionRate(conversionRate);
            stats.setRenewalRate(conversionRate); // Используем тот же показатель для renewal rate

            // Динамика
            stats.setSubscriberGrowth(getSubscriberGrowth(planId, monthAgo));
            stats.setRevenueGrowth(getRevenueGrowth(planId, monthAgo));

            return stats;
        } catch (Exception e) {
            e.printStackTrace();
            return new SubscriptionPlanStatisticsDto();
        }
    }

    private Map<String, Integer> getSubscriberGrowth(Long planId, LocalDateTime startDate) {
        Map<String, Integer> subscriberGrowth = new LinkedHashMap<>();
        subscriptionRepository.findSubscriberGrowthByPlanId(planId, startDate)
            .forEach(data -> {
                if (data[0] != null && data[1] != null) {
                    subscriberGrowth.put(
                        ((Timestamp) data[0]).toLocalDateTime().toString(),
                        ((Number) data[1]).intValue()
                    );
                }
            });
        return subscriberGrowth;
    }

    private Map<String, BigDecimal> getRevenueGrowth(Long planId, LocalDateTime startDate) {
        Map<String, BigDecimal> revenueGrowth = new LinkedHashMap<>();
        paymentRepository.findRevenueGrowthByPlanId(planId, startDate)
            .forEach(data -> {
                if (data[0] != null && data[1] != null) {
                    revenueGrowth.put(
                        ((Timestamp) data[0]).toLocalDateTime().toString(),
                        new BigDecimal(data[1].toString())
                    );
                }
            });
        return revenueGrowth;
    }

    @Transactional(readOnly = true)
    public ProductStatisticsDto getProductStatistics(Long productId) {
        LocalDateTime startOfMonth = LocalDateTime.now()
            .withDayOfMonth(1)
            .withHour(0)
            .withMinute(0)
            .withSecond(0)
            .withNano(0);

        // Используем BigDecimal.ZERO вместо null
        BigDecimal monthlyRevenue = paymentRepository.sumRevenueByProductIdAndPeriod(productId, startOfMonth);
        if (monthlyRevenue == null) {
            monthlyRevenue = BigDecimal.ZERO;
        }

        BigDecimal totalRevenue = paymentRepository.sumRevenueByProductId(productId);
        if (totalRevenue == null) {
            totalRevenue = BigDecimal.ZERO;
        }

        // Получаем количество активных подписок
        long activeSubscriptions = subscriptionRepository.countActiveByProductId(productId);
        
        // Получаем общее количество подписок
        long totalSubscriptions = subscriptionRepository.countByProductId(productId);
        
        // Вычисляем коэффициент продления
        double renewalRate = 0.0;
        if (totalSubscriptions > 0) {
            long renewedSubscriptions = subscriptionRepository.countByPlanProductIdAndRenewedAtIsNotNull(productId);
            renewalRate = (double) renewedSubscriptions / totalSubscriptions;
        }

        return ProductStatisticsDto.builder()
            .monthlyRevenue(monthlyRevenue)
            .totalRevenue(totalRevenue)
            .activeSubscriptions(activeSubscriptions)
            .totalSubscriptions(totalSubscriptions)
            .renewalRate(renewalRate)
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