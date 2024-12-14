package com.example.dto.stats;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class UserStatisticsDto {
    private int totalSubscriptions;
    private int activeSubscriptions;
    private BigDecimal totalSpent;
    private BigDecimal monthlySpending;
    private LocalDateTime memberSince;
    private double averageSubscriptionDuration;
    private List<String> subscribedProducts;
    private LocalDateTime lastSubscriptionDate;
    private int subscriptionRenewals;
    private boolean isPremiumUser;
} 