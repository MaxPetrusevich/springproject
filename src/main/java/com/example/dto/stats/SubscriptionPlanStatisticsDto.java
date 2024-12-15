package com.example.dto.stats;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionPlanStatisticsDto {
    private long totalSubscriptionsCount;
    private long activeSubscriptionsCount;
    private long completedSubscriptionsCount;
    private long canceledSubscriptionsCount;
    private double totalIncome;
    private double monthlyIncome;
    private double averageSubscriptionDuration;
    private double renewalRate;
    private int newSubscribersThisMonth;
    private int totalSubscribers;
    private long activeSubscriptions;
    private long totalSubscriptions;
    private BigDecimal monthlyRevenue;
    private BigDecimal totalRevenue;
    private double conversionRate;
    private Map<String, Integer> subscriberGrowth;
    private Map<String, BigDecimal> revenueGrowth;
} 