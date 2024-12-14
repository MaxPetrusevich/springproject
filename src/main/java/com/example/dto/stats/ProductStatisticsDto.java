package com.example.dto.stats;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class ProductStatisticsDto {
    private long totalSubscribers;
    private long activeSubscribers;
    private BigDecimal monthlyIncome;
    private BigDecimal totalIncome;
    private int numberOfPlans;
    private String mostPopularPlan;
    private double averageSubscriptionDuration;
    private double churnRate;
    private int newSubscribersThisMonth;
    private double growthRate;
} 