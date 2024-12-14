package com.example.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.Map;

@Data
public class PlanStatisticsDto {
    private long activeSubscriptions;
    private long totalSubscriptions;
    private double averageSubscriptionDays;
    private double renewalRate;
    private BigDecimal totalRevenue;
    private BigDecimal monthlyRevenue;
    private double conversionRate;
    private Map<String, Integer> subscriberGrowth;
    private Map<String, BigDecimal> revenueGrowth;
} 