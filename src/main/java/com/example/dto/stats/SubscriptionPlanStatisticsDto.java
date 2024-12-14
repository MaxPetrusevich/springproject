package com.example.dto.stats;

import lombok.Data;
import lombok.Builder;

@Data
@Builder
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
} 