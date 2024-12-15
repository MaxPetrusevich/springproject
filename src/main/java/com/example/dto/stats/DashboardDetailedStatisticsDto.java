package com.example.dto.stats;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

@Data
@Builder
public class DashboardDetailedStatisticsDto {
    private Map<String, Long> userGrowth;
    private Map<String, BigDecimal> revenueGrowth;
    private Map<String, Long> subscriptionGrowth;
    
    private double monthlyRecurringRevenue;
    private double annualRecurringRevenue;
    private double customerLifetimeValue;
    private double customerAcquisitionCost;
    private double churnRate;
    private double conversionRate;
    
    private Map<String, Integer> usersByRole;
    private Map<String, Integer> subscriptionsByStatus;
    private Map<String, BigDecimal> revenueByProduct;
    
    private Map<String, Double> growthTrends;
    private Map<String, Double> retentionRates;
    private Map<String, Double> engagementMetrics;
} 