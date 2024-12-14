package com.example.dto.stats;

import lombok.Data;
import lombok.Builder;
import java.util.List;

@Data
@Builder
public class OrganiserStatisticsDetailedDto {
    private long organisationsCount;
    private long productsCount;
    private long activeSubscriptionsCount;
    private long totalSubscriptionsCount;
    private long completedSubscriptionsCount;
    private long canceledSubscriptionsCount;
    private double monthlyIncome;
    private double totalIncome;
    private double averageSubscriptionDuration;
    private double renewalRate;
    private int newSubscribersThisMonth;
    private int totalSubscribers;
    
    // Данные для графиков
    private List<MonthlySubscriptionStats> monthlyStats;
    private List<ProductStats> productStats;
    
    @Data
    @Builder
    public static class MonthlySubscriptionStats {
        private String month;
        private int newSubscriptions;
        private int renewals;
        private int cancellations;
        private double income;
    }
    
    @Data
    @Builder
    public static class ProductStats {
        private String productName;
        private long activeSubscriptions;
        private double monthlyIncome;
        private double averageSubscriptionDuration;
    }
}