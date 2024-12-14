package com.example.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductStatisticsDto {
    private Long id;
    private String name;
    private long subscriptionPlansCount;
    private long activeSubscriptionPlansCount;
    private long activeSubscriptionsCount;
    private long totalSubscriptionsCount;
    private double monthlyIncome;
    private double totalIncome;
    private double renewalRate;
    private long newSubscriptionsThisMonth;
}