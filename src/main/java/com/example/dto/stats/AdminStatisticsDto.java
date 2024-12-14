package com.example.dto.stats;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class AdminStatisticsDto {
    private long totalUsers;
    private long totalOrganisations;
    private long totalProducts;
    private long totalSubscriptions;
    private long activeSubscriptions;
    private BigDecimal monthlyIncome;
    private BigDecimal yearlyIncome;
    private int newUsersThisMonth;
    private int newSubscriptionsThisMonth;
    private double averageSubscriptionDuration;
} 