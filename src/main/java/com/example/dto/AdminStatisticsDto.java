package com.example.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.Map;

@Data
public class AdminStatisticsDto {
    private long totalUsers;
    private long activeUsers;
    private long newUsersThisMonth;
    
    private long totalOrganisations;
    private long activeOrganisations;
    private long newOrganisationsThisMonth;
    
    private long totalProducts;
    private long activeProducts;
    private long newProductsThisMonth;
    
    private long totalSubscriptions;
    private long activeSubscriptions;
    private long newSubscriptionsThisMonth;
    
    private BigDecimal totalRevenue;
    private BigDecimal monthlyRevenue;
    private double conversionRate;
    
    private Map<String, Integer> userGrowth;
    private Map<String, Integer> organisationGrowth;
    private Map<String, Integer> productGrowth;
    private Map<String, BigDecimal> revenueGrowth;
    private Map<String, Integer> subscriptionGrowth;
} 