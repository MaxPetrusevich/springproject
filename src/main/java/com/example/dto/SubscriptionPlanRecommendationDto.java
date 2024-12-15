package com.example.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionPlanRecommendationDto {
    private BigDecimal recommendedPrice;
    private Integer recommendedPeriodDays;
    private String explanation;
    private Double expectedMonthlyRevenue;
    private Double conversionRate;
    private Double successProbability;
    private Double confidenceLevel;
    private Map<String, Double> historicalTrends;
    private Double seasonalFactor;
    private Double trendFactor;
} 